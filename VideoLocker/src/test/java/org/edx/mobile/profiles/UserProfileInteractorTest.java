package org.openedu.www.profiles;

import android.net.Uri;
import android.support.annotation.NonNull;

import tw.openedu.www.event.AccountUpdatedEvent;
import tw.openedu.www.event.ProfilePhotoUpdatedEvent;
import tw.openedu.www.http.RetroHttpException;
import tw.openedu.www.model.api.ProfileModel;
import tw.openedu.www.module.prefs.UserPrefs;
import org.openedu.www.test.BaseTest;

import tw.openedu.www.profiles.UserProfileImageViewModel;
import tw.openedu.www.profiles.UserProfileInteractor;
import tw.openedu.www.profiles.UserProfileViewModel;
import tw.openedu.www.user.Account;
import tw.openedu.www.user.LanguageProficiency;
import tw.openedu.www.user.ProfileImage;
import tw.openedu.www.user.UserAPI;
import tw.openedu.www.util.observer.Observer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import de.greenrobot.event.EventBus;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UserProfileInteractorTest extends BaseTest {

    @Mock
    private UserAPI userAPI;

    @Mock
    private UserPrefs userPrefs;

    private EventBus eventBus;

    private UserProfileInteractor interactor;

    @Mock
    private Observer<UserProfileViewModel> profileObserver;

    @Mock
    private Observer<UserProfileImageViewModel> imageObserver;

    @Before
    public void before() {
        eventBus = new EventBus();
    }

    @After
    public void after() {
        interactor.destroy();
    }

    private void createAndObserveInteractor() {
        interactor = new UserProfileInteractor(ProfileValues.USERNAME, userAPI, eventBus, userPrefs);
        interactor.observeProfile().subscribe(profileObserver);
        interactor.observeProfileImage().subscribe(imageObserver);
    }

    @Test
    public void getUsername_returnsUsernamePassedInConstructor() throws Exception {
        when(userAPI.getAccount(ProfileValues.USERNAME)).thenThrow(new RuntimeException());
        createAndObserveInteractor();
        assertThat(interactor.getUsername(), is(ProfileValues.USERNAME));
    }

    @Test
    public void isViewingOwnProfile_asAnonymousUser_returnsFalse() {
        configureBareMockAccount();
        createAndObserveInteractor();
        assertFalse(interactor.isViewingOwnProfile());
    }

    @Test
    public void isViewingOwnProfile_asUserBeingViewed_returnsTrue() {
        setAuthenticatedUsername(ProfileValues.USERNAME);
        configureBareMockAccount();
        createAndObserveInteractor();
        assertTrue(interactor.isViewingOwnProfile());
    }

    @Test
    public void whenProfileObserved_withGetAccountError_emitsError() throws Exception {
        final Throwable exception = new RuntimeException();
        when(userAPI.getAccount(ProfileValues.USERNAME)).thenThrow(exception);
        createAndObserveInteractor();
        verify(profileObserver).onError(exception);
    }

    @Test
    public void whenProfileObserved_withNoAboutMe_emitsNoAboutMe() throws Exception {
        configureBareMockAccount();
        createAndObserveInteractor();
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.NONE,
                null,
                null,
                UserProfileViewModel.ContentType.NO_ABOUT_ME,
                null)));
    }

    @Test
    public void whenProfileObserved_withNoAboutMe_withPrivateAccount_asAnonymousUser_emitsLimitedProfileMessageAndEmptyContent() throws Exception {
        final Account account = configureBareMockAccount();
        when(account.getAccountPrivacy()).thenReturn(Account.Privacy.PRIVATE);
        createAndObserveInteractor();
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.OTHER_USERS_PROFILE,
                null,
                null,
                UserProfileViewModel.ContentType.EMPTY,
                null)));
    }

    @Test
    public void whenProfileObserved_withNoAboutMe_withPrivateAccount_asUserBeingViewed_emitsIncompleteProfileMessageAndContent() throws Exception {
        setAuthenticatedUsername(ProfileValues.USERNAME);
        final Account account = configureBareMockAccount();
        when(account.getAccountPrivacy()).thenReturn(Account.Privacy.PRIVATE);
        createAndObserveInteractor();
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.OWN_PROFILE,
                null,
                null,
                UserProfileViewModel.ContentType.INCOMPLETE,
                null)));
    }

    @Test
    public void whenProfileObserved_withParentalConsentRequired_asUserBeingViewed_emitsParentalConsentRequired() {
        setAuthenticatedUsername(ProfileValues.USERNAME);
        final Account account = configureBareMockAccount();
        when(account.requiresParentalConsent()).thenReturn(true);
        createAndObserveInteractor();
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.OWN_PROFILE,
                null,
                null,
                UserProfileViewModel.ContentType.PARENTAL_CONSENT_REQUIRED,
                null)));
    }

    @Test
    public void whenProfileObserved_withParentalConsentRequired_asAnonymousUser_showsNoAboutMe() {
        final Account account = configureBareMockAccount();
        when(account.requiresParentalConsent()).thenReturn(true);
        createAndObserveInteractor();
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.OTHER_USERS_PROFILE,
                null,
                null,
                UserProfileViewModel.ContentType.NO_ABOUT_ME,
                null)));
    }

    @Test
    public void whenProfileImageObserved_withProfileImage_emitsFullImageUrl() {
        final Account account = configureBareMockAccount();
        final ProfileImage profileImage = account.getProfileImage();
        when(profileImage.hasImage()).thenReturn(true);
        when(profileImage.getImageUrlFull()).thenReturn(ProfileValues.ABSOLUTE_URL);
        createAndObserveInteractor();
        verify(imageObserver).onData(refEq(new UserProfileImageViewModel(Uri.parse(ProfileValues.ABSOLUTE_URL), true)));
    }

    @Test
    public void whenProfileImageObserved_withNoProfileImage_emitsNullPhotoUri() {
        configureBareMockAccount();
        createAndObserveInteractor();
        verify(imageObserver).onData(refEq(new UserProfileImageViewModel(null, true)));
    }

    @Test
    public void whenProfileObserved_withFullProfile_emitsLanguageAndLocationAndAboutMe() {
        final Account account = configureBareMockAccount();
        when(account.getBio()).thenReturn(ProfileValues.ABOUT_ME);
        when(account.getCountry()).thenReturn(ProfileValues.COUNTRY_CODE);
        when(account.getLanguageProficiencies()).thenReturn(Collections.singletonList(new LanguageProficiency(ProfileValues.LANGUAGE_CODE)));
        createAndObserveInteractor();
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.NONE,
                ProfileValues.LANGUAGE_NAME,
                ProfileValues.COUNTRY_NAME,
                UserProfileViewModel.ContentType.ABOUT_ME,
                ProfileValues.ABOUT_ME)));
    }

    @Test
    public void whenProfileObserved_withInvalidCountryCode_emitsProfileWithNoCountry() {
        final Account account = configureBareMockAccount();
        when(account.getCountry()).thenReturn(ProfileValues.INVALID_COUNTRY_CODE);
        createAndObserveInteractor();
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.NONE,
                null,
                null,
                UserProfileViewModel.ContentType.NO_ABOUT_ME,
                null)));
    }

    @Test
    public void whenProfileObserved_withInvalidLanguageCode_emitsProfileWithNoLanguage() {
        final Account account = configureBareMockAccount();
        when(account.getLanguageProficiencies()).thenReturn(Collections.singletonList(new LanguageProficiency(ProfileValues.INVALID_LANGUAGE_CODE)));
        createAndObserveInteractor();
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.NONE,
                null,
                null,
                UserProfileViewModel.ContentType.NO_ABOUT_ME,
                null)));
    }

    @Test
    public void onProfilePhotoUpdatedEvent_withMatchingUsername_emitsNewPhotoUri() {
        configureBareMockAccount();
        createAndObserveInteractor();
        verify(imageObserver).onData(refEq(new UserProfileImageViewModel(null, true)));
        final Uri newPhotoUri = Uri.parse(ProfileValues.ABSOLUTE_URL);
        eventBus.post(new ProfilePhotoUpdatedEvent(ProfileValues.USERNAME, newPhotoUri));
        verify(imageObserver).onData(refEq(new UserProfileImageViewModel(newPhotoUri, false)));
    }

    @Test
    public void onProfilePhotoUpdatedEvent_withUnrelatedUsername_emitsNewPhotoUri() {
        configureBareMockAccount();
        createAndObserveInteractor();
        verify(imageObserver).onData(refEq(new UserProfileImageViewModel(null, true)));
        eventBus.post(new ProfilePhotoUpdatedEvent(ProfileValues.ALTERNATE_USERNAME, Uri.parse(ProfileValues.ABSOLUTE_URL)));
        verifyNoMoreInteractions(imageObserver);
    }

    @Test
    public void onAccountUpdatedEvent_withMatchingUsername_emitsUpdatedProfileContent() throws Exception {
        final Account account = configureBareMockAccount();
        createAndObserveInteractor();
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.NONE,
                null,
                null,
                UserProfileViewModel.ContentType.NO_ABOUT_ME,
                null)));
        when(account.getBio()).thenReturn(ProfileValues.ABOUT_ME);
        eventBus.post(new AccountUpdatedEvent(account));
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.NONE,
                null,
                null,
                UserProfileViewModel.ContentType.ABOUT_ME,
                ProfileValues.ABOUT_ME)));
    }

    @Test
    public void onAccountUpdatedEvent_withUnrelatedUsername_emitsUpdatedProfileContent() throws Exception {
        final Account account = configureBareMockAccount();
        createAndObserveInteractor();
        verify(profileObserver).onData(refEq(new UserProfileViewModel(
                UserProfileViewModel.LimitedProfileMessage.NONE,
                null,
                null,
                UserProfileViewModel.ContentType.NO_ABOUT_ME,
                null)));
        when(account.getUsername()).thenReturn(ProfileValues.ALTERNATE_USERNAME);
        when(account.getBio()).thenReturn(ProfileValues.ABOUT_ME);
        eventBus.post(new AccountUpdatedEvent(account));
        verifyNoMoreInteractions(profileObserver);
    }

    private void setAuthenticatedUsername(@NonNull String username) {
        final ProfileModel profileModel = new ProfileModel();
        profileModel.username = username;
        when(userPrefs.getProfile()).thenReturn(profileModel);
    }

    @NonNull
    private Account configureBareMockAccount() {
        final Account account = mock(Account.class);
        when(account.getUsername()).thenReturn(ProfileValues.USERNAME);
        when(account.getProfileImage()).thenReturn(mock(ProfileImage.class));
        try {
            when(userAPI.getAccount(ProfileValues.USERNAME)).thenReturn(account);
        } catch (RetroHttpException e) {
            throw new RuntimeException(e);
        }
        return account;
    }
}
