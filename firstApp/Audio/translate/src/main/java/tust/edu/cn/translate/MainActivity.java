package tust.edu.cn.translate;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public class MainActivity extends AppCompatActivity {
    private Handler guiThread;
    private ExecutorService transThread;
    private Runnable updateTask;

    private EditText origText;
    private Future transPending;

    private TextView transText;
    private TextView retransText;

    private Spinner fromSpinner;
    private Spinner toSpinner;

    private TextWatcher textWatcher;
    private AdapterView.OnItemSelectedListener itemListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initThreading();
        findViews();
        setListenders();
    }
    private void findViews(){
        fromSpinner = (Spinner) findViewById(R.id.from_language);
        toSpinner   = (Spinner) findViewById(R.id.to_language);
        origText    = (EditText)findViewById(R.id.original_text);
        transText   = (TextView)findViewById(R.id.translated_text);
        retransText = (TextView)findViewById(R.id.retranslated_text);
    }
    //用于将数据源绑定到用户界面,就是将下拉列表中的内容添加进去
    private void setAdapters(){
        //使用的是默认布局
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,R.array.languages,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        //设置默认选项
        fromSpinner.setSelection(1);
        toSpinner.setSelection(1);
    }
    private void setListenders(){
        //定义事件监听
        textWatcher = new TextWatcher(){


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    queueUpdate(1000/*milli seconds*/);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        itemListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                queueUpdate(200);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        //为窗口中的窗体创建监听器
        origText.addTextChangedListener(textWatcher);
        fromSpinner.setOnItemSelectedListener(itemListener);
        toSpinner.setOnItemSelectedListener(itemListener);
    }
    //短暂暂停后进行更新
    private void queueUpdate(long milliSeconds){
        //如果先前的更新还没有开始，将其取消
        guiThread.removeCallbacks(updateTask);
        //开始一次新的更新
        guiThread.postDelayed(updateTask,milliSeconds);

    }
    //从选定的选项中提取语言代码
    private String getLang(Spinner spinner){
        String result = spinner.getSelectedItem().toString();
        int lparen  = result.indexOf("(");
        int rparen  = result.indexOf(")");
        result = result.substring(lparen+1,rparen);
        return result;
    }

    private void initThreading(){
        guiThread = new Handler();
        transThread = Executors.newSingleThreadExecutor();
        //在该任务中完成翻译并更新屏幕
        updateTask = new Runnable() {
            @Override
            public void run() {
                //获取翻译的文本
                String original = origText.getText().toString().trim();
                //清除之前翻译的文本
                if(transPending != null)
                    transPending.cancel(true);
                if(original.length()==0){
                    transText.setText(R.string.empty);
                    retransText.setText(R.string.empty);
                }else{
                    try{
                        //给用户的友好提示
                        transText.setText(R.string.translating);
                        retransText.setText(R.string.translating);

                        TranslateTask translateTask = new TranslateTask(
                                MainActivity.this,
                                original,
                                getLang(fromSpinner),
                                getLang(toSpinner)
                        );
                        transPending = transThread.submit(translateTask);
                    }catch(RejectedExecutionException e){
                        transText.setText(R.string.translation_error);
                        retransText.setText(R.string.translation_error);
                    }
                }
            }
        };
    }

    public void setTranslated(String text){
        guiSetText(transText,text);
    }
    public void setRetranslated(String text){
        guiSetText(retransText,text);
    }

    private void guiSetText(final TextView view,final String text){
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                view.setText(text);
            }
        });
    }
}

