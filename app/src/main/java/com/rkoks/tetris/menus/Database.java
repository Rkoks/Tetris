package com.rkoks.tetris.menus;

import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rkoks.tetris.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Database {

    //экземпляр баз данных
    private DatabaseReference dbName, dbWorld, dbPlayer;
    //Имя дерева с именами пользователей и UID
    private final String DB_NAME = "User";
    //Имя дерева с лучшим счётом каждого пользователя и UID
    private final String DB_WORLD = "World";
    //Имя дерева со счётом текущего пользователя
    private String DB_PLAYER = Auth.getUid();

    //получим ссылки на базы данных
    public Database() {
        dbName = FirebaseDatabase.getInstance().getReference(DB_NAME);
        dbWorld = FirebaseDatabase.getInstance().getReference(DB_WORLD);
        dbPlayer = FirebaseDatabase.getInstance().getReference(DB_PLAYER);
    }

    //присвоить имя пользователю
    public void writeName(String name){
        dbName.child(DB_PLAYER).setValue(new UserName(DB_PLAYER, name));
    }

    //получим имя если оно есть
    public void getUserName(final TextView view){
        Query nameQuery = dbName.child(DB_PLAYER);
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserName userNames = snapshot.getValue(UserName.class);
                    view.setText(userNames.name);
                } else {
                    view.setText("noName");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //запишем результат игры
    public void writeRecord(int score) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        String dataText = dateFormat.format(currentDate);
        dbPlayer.child(currentDate.toString()).setValue(new Record(dataText, score));
    }

    //выведем личные результаты игрока
    public void getRecord(final ListView view) {
        //выведем 20 лучших
        Query recordQuery = dbPlayer.orderByChild("score").limitToLast(20);
        recordQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                HashMap<String, String> map;
                long i = snapshot.getChildrenCount();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Record record = ds.getValue(Record.class);
                    map = new HashMap<>();
                    map.put("ID", i + "");
                    map.put("Score", record.score+"");
                    map.put("Date", record.date);
                    arrayList.add(0, map);
                    i--;
                }

                SimpleAdapter adapter = new SimpleAdapter(view.getContext(),
                        arrayList,
                        R.layout.rates_layout,
                        new String[]{"ID", "Score", "Date"},
                        new int[]{R.id.tvID, R.id.tvScore, R.id.tvDate});
                view.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //класс для сериализации данных пользователя
    private static class UserName {
        public String uid, name;

        public UserName(String uID, String name) {
            this.uid = uID;
            this.name = name;
        }

        public UserName() {
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    //класс для сериализации результатов игры
    private static class Record {
        public String date;
        public int score;

        public Record() {
        }

        public Record(String date, int score) {
            this.date = date;
            this.score = score;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

}
