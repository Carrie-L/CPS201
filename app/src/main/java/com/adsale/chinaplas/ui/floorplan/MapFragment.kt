package com.adsale.chinaplas.ui.floorplan


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.adsale.chinaplas.R
import java.math.BigDecimal
import android.app.Activity
import android.widget.Toast
import org.json.JSONException
import com.adsale.chinaplas.utils.LogUtil
import com.paypal.android.sdk.payments.*
import com.tencent.bugly.Bugly.applicationContext
import java.util.*
import kotlin.collections.HashSet
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
open class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //        try {
        //          val  mBraintreeFragment = BraintreeFragment.newInstance(requireContext() as AppCompatActivity?, mAuthorization)
        //            // mBraintreeFragment is ready to use!
        //        } catch (e: InvalidArgumentException) {
        //            // There was an issue with your authorization string.
        //        }
        val intent = Intent(requireActivity(), PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        requireActivity().startService(intent)

        view.findViewById<TextView>(com.adsale.chinaplas.R.id.button_paypal).setOnClickListener {
            onPressed()
        }
    }

    private fun onPressed() {
        /*
        * PAYMENT_INTENT_SALE will cause the payment to complete immediately.  立即付款
        * Change PAYMENT_INTENT_SALE to
        *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.   授权付款
        *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        *     later via calls from your server.
        *
        * Also, to include additional payment details and an item list, see getStuffToBuy() below.  其他付款明细和项目清单
        */
        val payment = getThingToBuy(PayPalPayment.PAYMENT_INTENT_ORDER)

        val intent = Intent(requireActivity(), PaymentActivity::class.java)
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
        startActivityForResult(intent, REQUEST_CODE_PAYMENT)
    }

    private fun getThingToBuy(paymentIntent: String): PayPalPayment {
        return PayPalPayment(
            BigDecimal("7.1"), "USD", "sample item",
            paymentIntent
        )
    }

    private /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */ val oauthScopes: PayPalOAuthScopes
        get() {
            val scopes = HashSet(
                Arrays.asList(
                    PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL,
                    PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS
                )
            )
            return PayPalOAuthScopes(scopes)
        }

    private fun displayResultText(result: String) {
        Toast.makeText(requireContext(), "Result :$result", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirm != null) {
                    try {
                        LogUtil.i(confirm.toJSONObject().toString(4))
                        LogUtil.i(confirm.payment.toJSONObject().toString(4))
                        /**
                         * TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        displayResultText("PaymentConfirmation info received from PayPal")

                        val id = "PAY-4EB71584A60313023LW3LZFY"
                        // 这里直接跟服务器确认支付结果，支付结果确认后回调处理结果
                        val jsonObject = confirm.toJSONObject()
                        if (jsonObject != null) {
                            val response = jsonObject.optJSONObject("response")
                            if (response != null) {
                                val id = response.optString("id")
                                LogUtil.i("id=$id")
                                //根据Id从自己的服务器判断相应的查询逻辑

                            }
                        }


                    } catch (e: JSONException) {
                        LogUtil.e("an extremely unlikely failure occurred: " + e.message)
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                LogUtil.i("The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                LogUtil.i(
                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
                )
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                val auth = data?.getParcelableExtra<PayPalAuthorization>(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION)
                if (auth != null) {
                    try {
                        LogUtil.i(auth.toJSONObject().toString(4))

                        val authorization_code = auth.authorizationCode
                        LogUtil.i(authorization_code)

                        sendAuthorizationToServer(auth)
                        displayResultText("Future Payment code received from PayPal")

                    } catch (e: JSONException) {
                        LogUtil.e("an extremely unlikely failure occurred: " + e.message)
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                LogUtil.i("The user canceled.")
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                LogUtil.i(

                    "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs."
                )
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                val auth = data?.getParcelableExtra<PayPalAuthorization>(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION)
                if (auth != null) {
                    try {
                        LogUtil.i(auth.toJSONObject().toString(4))
                        val authorizationCode = auth.authorizationCode
                        LogUtil.i(authorizationCode)
                        sendAuthorizationToServer(auth)
                        displayResultText("Profile Sharing code received from PayPal")
                    } catch (e: JSONException) {
                        LogUtil.e("an extremely unlikely failure occurred: " + e.message)
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                LogUtil.i("The user canceled.")
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                LogUtil.i(
                    "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs."
                )
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            LogUtil.i("The user canceled.")
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            LogUtil.i("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.")
        } else if (data == null) {
            LogUtil.i("data == null")
        }
    }

    private fun sendAuthorizationToServer(authorization: PayPalAuthorization) {

        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */

    }


    override fun onDestroy() {
        // Stop service when done
        requireActivity().stopService(Intent(requireActivity(), PayPalService::class.java))
        super.onDestroy()
    }


    companion object {
        /**
         * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
         * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
         * from https://developer.paypal.com
         * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
         * without communicating to PayPal's servers.
         */
        private val CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX

        // note that these credentials will differ between live & sandbox environments.
        private val CONFIG_CLIENT_ID = "AVDt1xaxzooCYndgtApPRY1tfeA1DAqtJ7bY0lh1OBpWc-ABu8deV5dy36KVVHy8aVpGrUc1-96aek8T"

        private val REQUEST_CODE_PAYMENT = 1
        private val REQUEST_CODE_FUTURE_PAYMENT = 2
        private val REQUEST_CODE_PROFILE_SHARING = 3

        private val config = PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"))
    }

    /*
     * Add app-provided shipping address to payment
     */
    private fun addAppProvidedShippingAddress(paypalPayment: PayPalPayment) {
        val shippingAddress = ShippingAddress()
            .recipientName("Mom Parker")
            .line1("52 North Main St.")
            .city("Austin")
            .state("TX")
            .postalCode("78729")
            .countryCode("US")
        paypalPayment.providedShippingAddress(shippingAddress)
    }

    /*
   * Enable retrieval of shipping addresses from buyer's PayPal account
   */
    private fun enableShippingAddressRetrieval(paypalPayment: PayPalPayment, enable: Boolean) {
        paypalPayment.enablePayPalShippingAddressesRetrieval(enable)
    }

    fun onFuturePaymentPressed() {
        val intent = Intent(requireActivity(), PayPalFuturePaymentActivity::class.java)

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT)
    }

    fun onProfileSharingPressed() {
        val intent = Intent(requireActivity(), PayPalProfileSharingActivity::class.java)

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, oauthScopes)

        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING)
    }

    fun onFuturePaymentPurchasePressed(pressed: View) {
        // Get the Client Metadata ID from the SDK
        val metadataId = PayPalConfiguration.getClientMetadataId(requireActivity())

        LogUtil.i("Client Metadata ID: $metadataId")

        // TODO: Send metadataId and transaction details to your server for processing with
        // PayPal...
        displayResultText("Client Metadata Id received from SDK")
    }

}
