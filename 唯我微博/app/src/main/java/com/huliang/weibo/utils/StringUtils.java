package com.huliang.weibo.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huliang.weibo.R;
import com.huliang.weibo.activity.UserInfoActivity;
import com.huliang.weibo.entity.Emotion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static SpannableString getWeiboContent(final Context context, final TextView tv, final String source) {
        String regexAt = "@[\u4e00-\u9fa5\\w]+";
        String regexTopic = "#[\u4e00-\u9fa5\\w]+#";
        String regexEmoji = "\\[[\u4e00-\u9fa5\\w]+\\]";
        final String website = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        String regex = "(" + regexAt + ")|(" + regexTopic + ")|(" + regexEmoji + ")|(" + website + ")";

        SpannableString spannableString = new SpannableString(source);

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(spannableString);

        if (matcher.find()) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            matcher.reset();
        }

        while (matcher.find()) {
            final String atStr = matcher.group(1);
            final String topicStr = matcher.group(2);
            String emojiStr = matcher.group(3);
            final String web = matcher.group(4);

            if (atStr != null) {
                int start = matcher.start(1);

                BoreClickableSpan clickableSpan = new BoreClickableSpan(context) {

                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(context, UserInfoActivity.class);
                        intent.putExtra("name", atStr.replace("@", ""));
                        context.startActivity(intent);
                    }
                };
                spannableString.setSpan(clickableSpan, start, start + atStr.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (topicStr != null) {
                int start = matcher.start(2);
                BoreClickableSpan clickableSpan = new BoreClickableSpan(context) {
                    @Override
                    public void onClick(View widget) {
                        ToastUtils.showToast(context, "话题: " + topicStr, Toast.LENGTH_SHORT);
                    }
                };
                spannableString.setSpan(clickableSpan, start, start + topicStr.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (emojiStr != null) {
                int start = matcher.start(3);
                int imgRes = Emotion.getImgByName(emojiStr);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgRes);
                if (bitmap != null) {
                    int size = (int) tv.getTextSize();
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

                    ImageSpan imageSpan = new ImageSpan(context, bitmap);
                    spannableString.setSpan(imageSpan, start, start + emojiStr.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            if (web != null) {
                final int start = matcher.start(4);
                BoreClickableSpan clickableSpan = new BoreClickableSpan(context) {
                    @Override
                    public void onClick(View widget) {
                        //调用浏览器打开网页
                        String url = source.substring(start, start + web.length());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                    }
                };
                spannableString.setSpan(clickableSpan, start, start + web.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    static class BoreClickableSpan extends ClickableSpan {

        private Context context;

        public BoreClickableSpan(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View widget) {
            // TODO Auto-generated method stub
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.txt_at_blue));
            ds.setUnderlineText(false);
        }
    }
}
