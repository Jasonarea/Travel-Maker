package com.ellalee.travelmaker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ellalee.travelmaker.R;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;

public class Sharing extends AppCompatActivity {

    //private String encoding = "UTF-8";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_link);

        Button btnBTShare = (Button)findViewById(R.id.btnBTShare);

        btnBTShare.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               FeedTemplate params = FeedTemplate
                       .newBuilder(ContentObject.newBuilder("travel",
                               "http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg",
                               LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                       .setMobileWebUrl("https://developers.kakao.com").build())
                               .setDescrption("Let's Travel Maker")
                               .build())
                       .setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20)
                               .setSharedCount(30).setViewCount(40).build())
                       .addButton(new ButtonObject("travel", LinkObject.newBuilder().setWebUrl("'https://developers.kakao.com").setMobileWebUrl("'https://developers.kakao.com").build()))
                       .addButton(new ButtonObject("maker", LinkObject.newBuilder()
                               .setWebUrl("'https://developers.kakao.com")
                               .setMobileWebUrl("'https://developers.kakao.com")
                               .setAndroidExecutionParams("key1=value1")
                               .setIosExecutionParams("key1=value1")
                               .build()))
                       .build();

               KakaoLinkService.getInstance().sendDefault(getApplicationContext(), params, new ResponseCallback<KakaoLinkResponse>() {
                   @Override
                   public void onFailure(ErrorResult errorResult) {
                       Logger.e(errorResult.toString());
                   }
                   @Override
                   public void onSuccess(KakaoLinkResponse result) {

                   }
               });
           }
        });
    }
}
