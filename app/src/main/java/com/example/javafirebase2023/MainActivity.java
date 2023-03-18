package com.example.javafirebase2023;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //https://umesh-dananjaya9.medium.com/firebase-realtime-crud-operations-for-android-studio-5621a64825f3

    EditText txtID, txtName, txtAdd, txtNum;
    Button btnSave, btnUpdate, btnDelete;

    Calisan calisan;

    ListView tumVeriler;
    ArrayList<String> veriAnahtarlari;
    ArrayAdapter<String> anahtarAdapter;
    String kayitSeciliAnahtar = ""; //seçili kaydın anahtar bilgisi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtID = findViewById(R.id.editTextTextPersonName);
        txtName = findViewById(R.id.editTextTextPersonName2);
        txtAdd = findViewById(R.id.editTextTextPersonName3);
        txtNum = findViewById(R.id.editTextTextPersonName4);

        btnSave = findViewById(R.id.button);
        btnUpdate = findViewById(R.id.button3);
        btnDelete = findViewById(R.id.button4);

        calisan = new Calisan();

        tumVeriler = findViewById(R.id.listele);
        veriAnahtarlari = new ArrayList<>();
        anahtarAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_activated_1, android.R.id.text1, veriAnahtarlari);
        veriAnahtarlariGetir();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Calisan");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(MainActivity.this, "Bulutta kayıt güncelleme oldu!", Toast.LENGTH_SHORT).show();
                    veriAnahtarlariGetir();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Hata:\n" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });

        //listeden seçim yapmak
        tumVeriler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                kayitSeciliAnahtar = adapterView.getItemAtPosition(position).toString();
                //seçili kayıt anahtarı saklayalım

                DatabaseReference readRef = FirebaseDatabase.getInstance().
                        getReference("Calisan").child(kayitSeciliAnahtar);

                readRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            txtID.setText(dataSnapshot.child("id").getValue().toString());
                            txtName.setText(dataSnapshot.child("ad").getValue().toString());
                            txtAdd.setText(dataSnapshot.child("adres").getValue().toString());
                            txtNum.setText(dataSnapshot.child("tel").getValue().toString());
                        } else
                            Toast.makeText(getApplicationContext(), "Gösterilecek kayıt yok!", Toast.LENGTH_SHORT).show();
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        //Yeni Kayıt Ekle
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference eklemedbRef = FirebaseDatabase.getInstance().getReference("Calisan");

                if (TextUtils.isEmpty(txtID.getText().toString().trim()))
                    Toast.makeText(getApplicationContext(), "ID boş geçilemez!", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(txtName.getText().toString().trim()))
                    Toast.makeText(getApplicationContext(), "Ad boş geçilemez!", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(txtNum.getText().toString().trim()))
                    Toast.makeText(getApplicationContext(), "Telefon boş geçilemez!", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(txtAdd.getText().toString().trim()))
                    Toast.makeText(getApplicationContext(), "Adres boş geçilemez!", Toast.LENGTH_SHORT).show();
                else {
                    calisan.setID(txtID.getText().toString().trim());
                    calisan.setAd(txtName.getText().toString().trim());
                    calisan.setAdres(txtAdd.getText().toString().trim());
                    calisan.setTel(txtNum.getText().toString().trim());

                    eklemedbRef.push().setValue(calisan);

                    Toast.makeText(getApplicationContext(), "Yeni kayıt başarılıdır.", Toast.LENGTH_SHORT).show();

                    veriAnahtarlariGetir(); //listeyi tazele
                }
            }
        });

        //Seçili kaydı güncelle
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference UpdateRef = FirebaseDatabase.getInstance().getReference("Calisan");
                UpdateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (!kayitSeciliAnahtar.trim().isEmpty() && dataSnapshot.hasChild(kayitSeciliAnahtar)) {
                                calisan.setID(txtID.getText().toString().trim());
                                calisan.setAd(txtName.getText().toString().trim());
                                calisan.setAdres(txtAdd.getText().toString().trim());
                                calisan.setTel(txtNum.getText().toString().trim());
                                DatabaseReference sonucdbRef = FirebaseDatabase.getInstance().getReference("Calisan").child(kayitSeciliAnahtar);
                                sonucdbRef.setValue(calisan);
                                Toast.makeText(getApplicationContext(), kayitSeciliAnahtar + "\nGüncelleme başarılıdır.", Toast.LENGTH_SHORT).show();

                                veriAnahtarlariGetir();
                            } else
                                Toast.makeText(getApplicationContext(), "Güncellenecek kayıt yok!", Toast.LENGTH_SHORT).show();
                        } catch (Exception exception) {
                            Toast.makeText(getApplicationContext(), "Güncellenecek kayıt seçilmedi!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        //Seçili kaydı sil
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("Calisan");
                deleteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (!kayitSeciliAnahtar.trim().isEmpty() && dataSnapshot.hasChild(kayitSeciliAnahtar)) {
                                DatabaseReference sildbRef = FirebaseDatabase.getInstance().getReference("Calisan").child(kayitSeciliAnahtar);
                                sildbRef.removeValue();
                                Toast.makeText(getApplicationContext(), kayitSeciliAnahtar + "\nSilme başarılıdır.", Toast.LENGTH_SHORT).show();

                                veriAnahtarlariGetir(); //listeyi tazele
                            } else
                                Toast.makeText(getApplicationContext(), "Silinecek kayıt yok!", Toast.LENGTH_SHORT).show();
                        } catch (Exception exception) {
                            Toast.makeText(getApplicationContext(), "Silinecek kayıt seçilmedi!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    //liste kutusunu güncelle
    private void veriAnahtarlariGetir() {
        DatabaseReference okuHepsiRef = FirebaseDatabase.getInstance().getReference("Calisan");
        okuHepsiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                veriAnahtarlari.clear();
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    veriAnahtarlari.add(datas.getKey());
                }
                tumVeriler.setAdapter(anahtarAdapter);

                txtID.setText("");
                txtName.setText("");
                txtAdd.setText("");
                txtNum.setText("");
                txtID.requestFocus();
                kayitSeciliAnahtar = "";
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}