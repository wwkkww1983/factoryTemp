package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityLoginInternationalBinding;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.viewmodel.user.InternationalLoginViewModel;

import java.util.ArrayList;

/**
 * Created by wangmengsi on 2018/2/26.
 */

public class LoginInternationalActivity extends BaseViewModelActivity<ActivityLoginInternationalBinding, InternationalLoginViewModel> {

    private static final int RC_SIGN_IN = 0x0001;
    private GoogleApiClient mGoogleClient;
    private GoogleSignInClient mGoogleSignInClient;


    private CallbackManager mCallbackManager;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_login_international;
    }

    @Override
    protected void init() {
        super.init();
        binding.setViewModle(viewmodel);
        GoogleSignInOptions mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("915782849953-dvko1hvf40v70hg6gp637qf2pfrcgd12.apps.googleusercontent.com")
                .build();

        mGoogleClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> BlackToast.show("Login Failed"))
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, mGso);
    }


    @Override
    protected void initView() {
        super.initView();
        LoginManager.getInstance().logOut();
        mCallbackManager = CallbackManager.Factory.create();
      /*  binding.faceBook.setReadPermissions(Arrays.asList(EMAIL, USER_POSTS));
        binding.faceBook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        (object, response) -> viewmodel.loginFaceBook( object.optString("id"), loginResult.getAccessToken().getToken()));
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                BlackToast.show("Login Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                BlackToast.show("Login Failed");
            }
        });*/


        binding.idLoginBtn.setOnClickListener(v -> IntentUtils.goToLoginByEmail(mContext));
        binding.idRegistBtn.setOnClickListener(v -> IntentUtils.goToRegistInternationnal(mContext));


        binding.idFacebookLoginBtn.setOnClickListener(v -> facebookBtnClick());

        binding.idGoogleLoginBtn.setOnClickListener(v -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                viewmodel.loginGoogle(acct.getId(), acct.getIdToken());
            } else {
                BlackToast.show("Login Failed");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleSignInClient.signOut();
        mGoogleClient.stopAutoManage(this);
        mGoogleClient.disconnect();
    }

    @Override
    protected InternationalLoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(InternationalLoginViewModel.class);
    }


    /**
     * 点击facebook登录按钮
     */
    public void facebookBtnClick() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("public_profile");
        LoginManager.getInstance().logInWithReadPermissions(this, permissions);
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),//obect.optString("email")
                        (object, response) -> viewmodel.loginFaceBook(object.optString("id"), loginResult.getAccessToken().getToken()));
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                BlackToast.show("Login Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                BlackToast.show("Login Failed");
            }
        });
    }
}
