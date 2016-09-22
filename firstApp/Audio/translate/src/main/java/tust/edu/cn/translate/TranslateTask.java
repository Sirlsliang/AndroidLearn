package tust.edu.cn.translate;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by MyPC on 2016/9/22.
 */
public class TranslateTask implements Runnable {
    private static final String TAG = TranslateTask.class.getSimpleName();
    private final MainActivity mainActivity;
    private final String  original,from,to;

    public TranslateTask(MainActivity mainActivity,String original,String from,String to){
        this.mainActivity = mainActivity;
        this.original = original;
        this.from = from;
        this.to = to;
    }
    @Override
    public void run() {
        String trans = doTranslate(original,from,to);
        mainActivity.setTranslated(trans);

        String retrans = doTranslate(trans,to,from);
        mainActivity.setRetranslated(retrans);
    }

    private String doTranslate(String original,String from,String to){
        String result = mainActivity.getResources().getString(R.string.translation_error);
        HttpURLConnection con = null;
        Log.d(TAG,"doTranslate("+original+", "+from+", "+to+")");
        try{
            //检测是否被打断
            if(Thread.interrupted())
                throw new InterruptedException();
            String q = URLEncoder.encode(original,"UTF-8");
            URL url = new URL(
                    "http://ajax.googleapis.com/ajax/services/language/translate"
                    +"?v=1.0"+"&q="+q+"langpair="+from+"%7C"+to);
            con = (HttpURLConnection)url.openConnection();
            con.setReadTimeout(10000/*milliseconds*/);
            con.setRequestMethod("GET");
            con.addRequestProperty("Referer","http://www.pragprog.com/titles/eband3/hello-android");
            con.setDoInput(true);
            con.connect();

            if(Thread.interrupted())
                throw  new InterruptedException();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            String payload = reader.readLine();
            reader.close();

            JSONObject jsonObject = new JSONObject(payload);
            result = jsonObject.getJSONObject("responseData").getString("translatedText")
                        .replace("&#39;","'").replace("&amp","&");
            if(Thread.interrupted())
                throw  new InterruptedException();

        }catch (IOException e){

        }catch (JSONException e){

        }catch (InterruptedException e){

        }finally {
            if(con!=null)
                con.disconnect();
        }
        return result;
    }
}
