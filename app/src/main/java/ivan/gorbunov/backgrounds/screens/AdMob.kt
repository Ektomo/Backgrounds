package ivan.gorbunov.backgrounds.screens

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import ivan.gorbunov.backgrounds.R

@Composable
fun AdMobView(modifier: Modifier = Modifier, type: AdMobType) {
    val isInEditMode = LocalInspectionMode.current
    if (isInEditMode) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(vertical = 6.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            text = "Advert Here",
        )
    } else {
        when(type){
            AdMobType.Native -> {
                AndroidView(
                    factory = {context ->
                        AdView(context).apply {
                        }
                        var nativeAdNullable: NativeAd? = null
                        AdLoader.Builder(context, "ca-app-pub-3940256099942544/6300978111").
                                forNativeAd {
                                    nativeAdNullable = it
                                }.withAdListener(object : AdListener(){

                                }).withNativeAdOptions(NativeAdOptions.Builder().build())
                            .build()
                        if(nativeAdNullable != null){
                            NativeAdView(context).apply {
                                if (nativeAdNullable != null) setNativeAd(nativeAdNullable!!)
                                mediaView = MediaView(context)

                            }
                        }else{
                            View(context)
                        }
                    }
                )
            }
            AdMobType.Banner -> {
                AndroidView(
                    modifier = modifier.fillMaxWidth(),
                    factory = { context ->
                        AdView(context).apply {
                            adSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
                            adUnitId = "ca-app-pub-3940256099942544/6300978111"
                            loadAd(AdRequest.Builder().build())
                            this.adListener = object : AdListener(){

                            }

                        }

                    }
                )
            }
        }

    }
}

enum class AdMobType{
    Native, Banner
}