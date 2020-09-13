package com.rkoks.tetris.menus;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Auth {
    private static FirebaseAuth mAuth;
    private static FirebaseUser currentUser;

    //подключаемся к сервису аутетнтификации
    public static void doAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    //проверяем регистрацию пользователя
    public static void regUser(final Context context) {
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { //регистрируем
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Registered", Toast.LENGTH_SHORT).show();
                        currentUser = mAuth.getCurrentUser();
                    } else {
                        Toast.makeText(context, "Reg Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else { //показываем существующего
            getUid(context);

        }
    }

    //отображение пользователя
    public static void getUid(Context context){
        String userId = currentUser.getUid();
    }
    public static String getUid(){
        return currentUser.getUid();
    }

}
