package io.brahmaos.samples.walletmanagersample;

import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.List;

import brahmaos.app.WalletManager;
import brahmaos.content.BrahmaContext;
import brahmaos.content.WalletData;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "WalletManagerSample";

    private EditText mEtCreateName, mEtCreatePassword;
    private Button mBtnCreate;

    private EditText mEtDeleteAddress;
    private Button mBtnDelete;

    private EditText mEtPrivateKey, mEtMnemonics, mEtKeyStore;
    private Button mBtnImport;

    private EditText mEtUpdateAddress, mEtOldPwd, mEtNewPwd;
    private Button mBtnUpdateWallet;

    private EditText mEtBlanceAddress;
    private TextView mTvBlance;
    private Button mBtnGetBlance;

    private EditText mEtGasPrice, mEtGasLimit;
    private TextView mEtEtcHash, mEtKncHash;
    private Button mBtnTransfer;

    private EditText mEtEthTransHash;
    private TextView mTvEthTransaction;
    private Button mBtnEthTransaction;

    private WalletManager mWalletManager;
    private List<WalletData> mWalletList;

    private static final String ROPSTEN_URL = "https://ropsten.infura.io/Gy3Csyt4bzKIGsctm3g0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWalletManager = (WalletManager) getSystemService(BrahmaContext.WALLET_SERVICE);

        mEtCreateName = (EditText) findViewById(R.id.et_create_name);
        mEtCreatePassword = (EditText) findViewById(R.id.et_create_password);
        mEtDeleteAddress = (EditText) findViewById(R.id.et_delete_address);

        mEtPrivateKey = (EditText) findViewById(R.id.et_private_key);
        mEtMnemonics = (EditText) findViewById(R.id.et_mnemonics);
        mEtKeyStore = (EditText) findViewById(R.id.et_keyStore);

        mEtUpdateAddress = (EditText) findViewById(R.id.et_update_address);
        mEtOldPwd = (EditText) findViewById(R.id.et_old_pwd);
        mEtNewPwd = (EditText) findViewById(R.id.et_new_pwd);
        mEtBlanceAddress = (EditText) findViewById(R.id.et_blance_address);

        mTvBlance = (TextView) findViewById(R.id.tv_blance);

        mBtnCreate = (Button) findViewById(R.id.btn_create);
        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mWalletList = mWalletManager.createWallet(
                                mEtCreateName.getText().toString().trim(),
                                mEtCreatePassword.getText().toString().trim());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mWalletList != null) {
                                    Toast.makeText(MainActivity.this,
                                            "have created " + mWalletList.size() + " wallsts.", Toast.LENGTH_SHORT).show();
                                }
                                for (WalletData data : mWalletList) {
                                    String mnemonics = mWalletManager.decryptMnemonics(
                                            data.mnemonicStr, mEtCreatePassword.getText().toString().trim());
                                    Log.d(TAG, "mnemonics are: " + mnemonics);
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        mBtnDelete = (Button) findViewById(R.id.btn_delete);
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<WalletData> ethWallets =
                        mWalletManager.getWalletsForChainType(WalletManager.WALLET_CHAIN_TYPE_ETH);
                Log.d(TAG,"get " + (ethWallets == null ? "null" : ethWallets.size()) + "ETH wallets");

                int res = mWalletManager.deleteWalletByAddress(mEtDeleteAddress.getText().toString().trim());
                Toast.makeText(MainActivity.this,
                        "deleted " + (res == WalletManager.CODE_NO_ERROR ? "success" : "failed"), Toast.LENGTH_SHORT).show();
            }
        });

        mBtnImport = (Button) findViewById(R.id.btn_import);
        mBtnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mEtPrivateKey.getText() != null && !mEtPrivateKey.getText().toString().isEmpty()) {
                            mWalletManager.importEthereumWallet(
                                    "importPK",
                                    "qqqqqq",
                                    mEtPrivateKey.getText().toString().trim(),
                                    WalletManager.IMPORT_BY_PRIVATE_KEY);
                        } else if (mEtMnemonics.getText() != null && !mEtMnemonics.getText().toString().isEmpty()) {
                            mWalletManager.importEthereumWallet(
                                    "importMN",
                                    "qqqqqq",
                                    mEtMnemonics.getText().toString().trim(),
                                    WalletManager.IMPORT_BY_MNEMONICS);
                        } else if (mEtKeyStore.getText() != null && !mEtKeyStore.getText().toString().isEmpty()) {
                            mWalletManager.importEthereumWallet(
                                    "importKS",
                                    "qqqqqq",
                                    mEtKeyStore.getText().toString().trim(),
                                    WalletManager.IMPORT_BY_MNEMONICS);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Import completed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });

        mBtnUpdateWallet = (Button) findViewById(R.id.btn_update_wallet);
        mBtnUpdateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WalletData walletData = mWalletManager.getWalletDataByAddress(
                                mEtUpdateAddress.getText().toString().trim());
                        final int nameResult = mWalletManager.updateWalletNameForAddress("update" + System.currentTimeMillis(),
                                mEtUpdateAddress.getText().toString().trim());
                        final int passwordResult = mWalletManager.updateEthereumWalletPassword(
                                mEtUpdateAddress.getText().toString().trim(),
                                mEtOldPwd.getText().toString().trim(),
                                mEtNewPwd.getText().toString().trim());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,nameResult + "; " + passwordResult, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });

        mBtnGetBlance = (Button) findViewById(R.id.btn_get_blance);
        mBtnGetBlance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ropstenUrl eth & knc
                mWalletManager.getEthereumAccountBalanceByAddress(
                        ROPSTEN_URL, "0x62461ec66ea7014833181e8e22a8e64f40de34ec",
                        new GetETHBlanceListener());

                //mainnet url
                mWalletManager.getEthereumAccountBalanceByAddress(
                        "0x9021a9f123e5785ba3d1e121a942802a4f822486", new GetETHBlanceListener());
            }
        });

        mEtEtcHash = (TextView) findViewById(R.id.et_etc_hash);
        mEtKncHash = (TextView) findViewById(R.id.et_knc_hash);
        mEtGasPrice = (EditText) findViewById(R.id.et_gas_price);
        mEtGasLimit = (EditText) findViewById(R.id.et_gas_limit);
        mEtGasPrice.setText("20");
        mEtGasLimit.setText("400000");
        mBtnTransfer = (Button)findViewById(R.id.btn_transfer);
        mBtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //ETH ropsten
                        final String etcHash = mWalletManager.transferEthereum(ROPSTEN_URL,
                                "0x4029a7e31ae310479784e9ade9e3172698807341",
                                "","qqqqqq",
                                "0x62461ec66ea7014833181e8e22a8e64f40de34ec", 0.1,
                                new BigInteger(mEtGasPrice.getText().toString().trim()).doubleValue(),
                                Long.valueOf(mEtGasLimit.getText().toString().trim()),
                                "test");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null == etcHash || etcHash.isEmpty()) {
                                    mEtEtcHash.setText("error");
                                } else {
                                    mEtEtcHash.setText(etcHash);
                                }
                            }
                        });
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //KNC ropsten
                        final String kncHash = mWalletManager.transferEthereum(ROPSTEN_URL,
                                "0x4029a7e31ae310479784e9ade9e3172698807341",
                                "0x4E470dc7321E84CA96FcAEDD0C8aBCebbAEB68C6","qqqqqq",
                                "0x62461ec66ea7014833181e8e22a8e64f40de34ec", 1,
                                new BigInteger(mEtGasPrice.getText().toString().trim()).doubleValue(),
                                Long.valueOf(mEtGasLimit.getText().toString().trim()),
                                "test");runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null == kncHash || kncHash.isEmpty()) {
                                    mEtKncHash.setText("error");
                                } else {
                                    mEtKncHash.setText(kncHash);
                                }
                            }
                        });
                    }
                }).start();
            }
        });


        mEtEthTransHash = (EditText) findViewById(R.id.et_trans_hash);
        mTvEthTransaction = (TextView) findViewById(R.id.tv_transaction);
        mBtnEthTransaction = (Button) findViewById(R.id.btn_transaction);
        mBtnEthTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEtEthTransHash.getText() != null &&
                        !mEtEthTransHash.getText().toString().isEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String result = mWalletManager.getEthereumTransactionByHash(
                                    mEtEthTransHash.getText().toString().trim());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (null ==result || result.isEmpty()) {
                                        mTvEthTransaction.setText("error");
                                    } else {
                                        mTvEthTransaction.setText(result);
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    private class GetETHBlanceListener implements WalletManager.OnETHBlanceGetListener {
        private String threadName = null;
        public GetETHBlanceListener() {
            threadName = Process.myPid() + ", " + Process.myTid();
            Log.d(TAG, "new " + threadName);
        }

        @Override
        public void onETHBlanceGetError() {
            Log.d(TAG, "onETHBlanceGetError---" + Process.myTid());
            mTvBlance.setText("error");
        }

        @Override
        public void onETHBlanceGetSuccess(String s) {
            final String text =((null == s || s.isEmpty())?"empty":s);
            Log.d(TAG, "onETHBlanceGetSuccess---" + Process.myTid() + ": " + text);
            mTvBlance.setText(text);
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        }
    }
}
