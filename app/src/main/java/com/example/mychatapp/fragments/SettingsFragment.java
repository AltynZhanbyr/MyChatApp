//package com.example.mychatapp.fragments;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceFragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.preference.CheckBoxPreference;
//import androidx.preference.EditTextPreference;
//import androidx.preference.ListPreference;
//import androidx.preference.Preference;
//import androidx.preference.PreferenceFragmentCompat;
//import androidx.preference.PreferenceScreen;
//
//import com.example.mychatapp.R;
//import com.example.mychatapp.activities.UsersActivity;
//import com.example.mychatapp.model.ModelUser;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
//
//    private String userName;
//    private String userKey;
//
//    private ModelUser user;
//
//    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference databaseReference;
//
//
//    private SharedPreferences.Editor editor;
//    private SharedPreferences.Editor monitorEditor;
//    private SharedPreferences preferences;
//
//    @Override
//    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        addPreferencesFromResource(R.xml.settings);
//
//
//        firebaseDatabase=FirebaseDatabase.getInstance();
//        databaseReference=firebaseDatabase.getReference().child("users");
//
//
//        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
//        PreferenceScreen preferenceScreen = getPreferenceScreen();
//
//        int count = preferenceScreen.getPreferenceCount();
//
//        for (int i = 0; i < count; i++) {
//            Preference preference = preferenceScreen.getPreference(i);
//            if ((preference instanceof EditTextPreference)) {
//                initSummaryValue(preference, sharedPreferences.getString(preference.getKey(),""));
//            }
//        }
//    }
//    private void initSummaryValue(Preference preference, String value) {
//        if (preference instanceof EditTextPreference) {
//            preference.setSummary(value);
//            setName(value);
//        }
//    }
//
//
//
//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//        Preference preference = findPreference(s);
//        if (preference instanceof EditTextPreference) {
//            String value = sharedPreferences.getString(preference.getKey(), "");
//            preference.setSummary(value);
//            setName(value);
//        }
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        preferences=getActivity().getSharedPreferences("UserSettings",getActivity().MODE_PRIVATE);
//        editor=preferences.edit();
//
//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }
//
//    public void onDestroyView() {
//        super.onDestroyView();
//        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
//    }
//    public void setName(String value) {
//        databaseReference.orderByChild("id")
//                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot childSnapshot: snapshot.getChildren()) {
//                            userKey = childSnapshot.getKey();
//                        }
//
//                        databaseReference.child(userKey).child("name").setValue(value);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//    }
////    private void setUser(){
////
////        databaseReference.orderByChild("id")
////                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
////                .addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(@NonNull DataSnapshot snapshot) {
////                        for (DataSnapshot childSnapshot: snapshot.getChildren()) {
////                            user = childSnapshot.getValue(ModelUser.class);
////                        }
////                        Toast.makeText(getContext(),user.toString(),Toast.LENGTH_SHORT).show();
////                    }
////
////                    @Override
////                    public void onCancelled(@NonNull DatabaseError error) {
////
////                    }
////                });
////
////    }
//}
