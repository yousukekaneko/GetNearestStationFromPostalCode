package com.example.android.sample.getneareststationfrompostalcode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //log interceptorの設定
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        //retrofitインスタンスを生成
        val retrofit = Retrofit.Builder()
            .baseUrl("http://geoapi.heartrails.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        val service = retrofit.create(HeartRailsService::class.java)

        //クリック処理
        button.setOnClickListener {
            var inputAddress = editTextTextPostalAddress.text.toString()

            if (!isValidPostAddress(inputAddress)) {
                editTextTextPostalAddress.error = getString(R.string.invalid_PostAddress)
                return@setOnClickListener
            }

//            //TODO 入力した値が正しいかどうかの処理
//            if (inputAddress.length != 7) {
//                val invalidDialog = AlertDialog.Builder(this@MainActivity).apply{
//                    setTitle(getString(R.string.dialog_title))
//                    setMessage(getString(R.string.dialog_message))
//                    setPositiveButton(getString(R.string.answer_yes)) { _, _ ->
//                    }
//                    show()
//                }
//            }

            val location = service.apiGet("getStations", inputAddress)
            location.enqueue(object : retrofit2.Callback<PostalResponse> {

                override fun onResponse(
                    call: Call<PostalResponse>,
                    response: Response<PostalResponse>
                ) {
                    Log.d("通信結果", "成功!!!")

                    //mapを使わないパターン
//                    var text = ""
//                    val eachAddress = response.body()?.response?.location
//                    if (eachAddress != null) {
//                        for (i in eachAddress) {
//                            text += "都道府県 : ${i.prefecture}\n区 : ${i.city}\n番地 : ${i.town}\n\n"
//                        }
//                    }
//                    resultLabel.text = text

                    //mapを使ったパターン
                    val eachAddress : List<Station>? = response.body()?.response?.station

                    resultTextView.text =
                        eachAddress?.map {
                            "${it.name}"
                        }?.joinToString()

                    if (eachAddress == null) {
                        resultTextView.text = getString(R.string.no_address_message)
                    }
                }
                override fun onFailure(call: Call<PostalResponse>, t: Throwable) {
                    Log.d("通信結果", "失敗 $t")
                }
            })
        }
    }


    private fun isValidPostAddress(inputAddress: String): Boolean {
        val addressPattern = Regex(pattern = """\d{3}\d{4}""").toString()
        val pattern = Pattern.compile(addressPattern)
        val matcher = pattern.matcher(inputAddress)
        return matcher.matches()
    }
}