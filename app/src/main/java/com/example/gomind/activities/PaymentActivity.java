package com.example.gomind.activities;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gomind.R;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;



public class PaymentActivity extends AppCompatActivity {

//    private final ActivityResultLauncher<Intent> tokenizeLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    showToken(result.getData());
//                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
//                    showError();
//                }
//            }
//    );
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initUi();
//    }
//
//    private void initUi() {
//        setContentView(R.layout.activity_payment);
//        findViewById(R.id.tokenizeButton).setOnClickListener(v -> onTokenizeButtonClick());
//    }
//
//    private void onTokenizeButtonClick() {
//        Set<PaymentMethodType> paymentMethodTypes = new HashSet<>();
//        paymentMethodTypes.add(PaymentMethodType.GOOGLE_PAY);
//        paymentMethodTypes.add(PaymentMethodType.BANK_CARD);
//        paymentMethodTypes.add(PaymentMethodType.SBERBANK);
//        paymentMethodTypes.add(PaymentMethodType.YOO_MONEY);
//        paymentMethodTypes.add(PaymentMethodType.SBP);
//
//        PaymentParameters paymentParameters = new PaymentParameters(
//                new Amount(BigDecimal.valueOf(10.0), Currency.getInstance("RUB")),
//                getString(R.string.main_product_name),
//                getString(R.string.main_product_description),
//                BuildConfig.MERCHANT_TOKEN,
//                BuildConfig.SHOP_ID,
//                SavePaymentMethod.OFF,
//                paymentMethodTypes,
//                BuildConfig.GATEWAY_ID,
//                getString(R.string.test_redirect_url),
//                getString(R.string.test_phone_number),
//                new GooglePayParameters(),
//                BuildConfig.CLIENT_ID
//        );
//
//        Intent intent = createTokenizeIntent(this, paymentParameters, new TestParameters(true));
//        tokenizeLauncher.launch(intent);
//    }
//
//    private void showToken(Intent data) {
//        if (data != null) {
//            String token = createTokenizationResult(data).getPaymentToken();
//            Toast.makeText(
//                    this,
//                    String.format(Locale.getDefault(), getString(R.string.tokenization_success), token),
//                    Toast.LENGTH_LONG
//            ).show();
//        } else {
//            showError();
//        }
//    }
//
//    private void showError() {
//        Toast.makeText(this, R.string.tokenization_canceled, Toast.LENGTH_SHORT).show();
//    }
//}
}