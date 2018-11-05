package io.brahmaos.samples.walletmanagersample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.app.WalletManager;
import android.content.pm.WalletData;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "WalletManagerSample";

    private EditText mEtCreateName;
    private EditText mEtCreatePassword;

    private WalletManager mWalletManager;
    private List<WalletData> mWalletList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get WalletManager service.
        mWalletManager = (WalletManager) getSystemService("wallet");

        mEtCreateName = findViewById(R.id.et_create_name);
        mEtCreatePassword = findViewById(R.id.et_create_password);
        Button mBtnCreate = findViewById(R.id.btn_create);
        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWalletList = mWalletManager.createWallet(
                        mEtCreateName.getText().toString().trim(),
                        mEtCreatePassword.getText().toString().trim());
                if (mWalletList != null) {
                    Toast.makeText(MainActivity.this,
                            "have created " + mWalletList.size() + " wallets.", Toast.LENGTH_LONG).show();
                }
                for (WalletData data : mWalletList) {
                    String mnemonics = mWalletManager.decryptMnemonics(
                            data.mnemonicStr, mEtCreatePassword.getText().toString().trim());
                    Log.d(TAG, "mnemonics are: " + mnemonics);
                }
            }
        });
    }
}
