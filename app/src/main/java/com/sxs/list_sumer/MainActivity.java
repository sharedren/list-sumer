package com.sxs.list_sumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends Activity {
    private InnerGridView grid;

    private Adapter adapter;
    public int gnum = 0;
    public double[] gpreValue = new double[100];
    public double sum = 0.0;

    private List<goods> list = new ArrayList<goods>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    // 要申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//sxs
                    // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取 30
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // 检查该权限是否已经获取 33
                        int i = ContextCompat.checkSelfPermission(this, permissions[0]);// 权限是否已经 授权 GRANTED---授权 DINIED---拒绝 35
                        if (i != PackageManager.PERMISSION_GRANTED) { // 如果没有授予该权限，就去提示用户请求 37
                            ShowDialogTipUserRequestPermission();
                        }
                    }

        final Button ok = (Button) findViewById(R.id.button1);//添加行按钮
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /*
                for(int i=0;i<list.size();i++){
					String str=list.get(i).getName();
					if(!str.equalsIgnoreCase("")){
						System.out.println("输入的值"+str);
					}
				}
				*/
                //测试按钮的反应
                //Toast.makeText(MainActivity.this, "fuck", Toast.LENGTH_SHORT).show();

                //添加一行
                goods model = new goods();
                model.setName(Integer.toString(gnum++));
                //model.setName2(Double.toString(gpreValue));
                list.add(model);

                grid = (InnerGridView) findViewById(R.id.gridView1);
                adapter = new Adapter();
                grid.setAdapter(adapter);
//
//                setContentView(R.layout.item);
//                EditText et = (EditText) findViewById(R.id.editText2);
//                et.requestFocus();
            }
        });

        final Button btn_save = (Button) findViewById(R.id.buttonSave);//保存按钮
        btn_save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //测试按钮的反应
                //Toast.makeText(MainActivity.this, "fuck", Toast.LENGTH_SHORT).show();

                //打印数组内的值即可，注意剔除0值，重新排序

                //获取默认外部存储器根目录
                File sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();
                String mLocalExternalPath = sdcard.getAbsolutePath();
                //Toast.makeText(MainActivity.this, mLocalExternalPath, Toast.LENGTH_SHORT).show();
                if (!sdcard.canWrite()) {
                    Toast.makeText(MainActivity.this, "must open privilege STORAGE", Toast.LENGTH_SHORT).show();
                } else {

                    //创建list-sumer文件夹
                    File folder = new File(mLocalExternalPath + File.separator + "list-sumer" + File.separator);
                    if (!folder.exists()) {
                        boolean bool = folder.mkdirs();
                        if (bool == false) {
//                        if (!sdcard.canWrite()) {
//                            Toast.makeText(MainActivity.this, "must open privilege STORAGE", Toast.LENGTH_SHORT).show();
//                        }
                        }
                        // File directoryCreated = new File(folder, "filename");
                        //Toast.makeText(MainActivity.this, "create folder:" + folder.getPath(), Toast.LENGTH_SHORT).show();
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间作为文件名
                    String filepath = folder.getPath() + File.separator + formatter.format(curDate) + ".txt";
/*
                ReadOrWriteObject rOd = new ReadOrWriteObject(filepath);
                rOd.openFile(ReadOrWriteObject.FileWrite);
                */

                    File outFile = new File(filepath);
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(outFile);
                        double outSave[] = new double[100];
                        int outSaveLen = 0;
                        String blank = "    ";
                        String line = "\n";

                        for (int i = 0, j = 0; i < 100; i++) {
                            if (gpreValue[i] != 0.00) {
                                outSave[j] = gpreValue[i];

                                String strTmp = Integer.toString(j);
                                outStream.write(strTmp.getBytes(), 0, strTmp.getBytes().length);
                                outStream.write(blank.getBytes(), 0, blank.getBytes().length);
                                strTmp = Double.toString(outSave[j]);
                                outStream.write(strTmp.getBytes(), 0, strTmp.getBytes().length);
                                outStream.write(line.getBytes(), 0, line.getBytes().length);

                                j++;
                                outSaveLen = j;
                            }
                        }
                        String strSum = "SUM:";
                        outStream.write(strSum.getBytes(), 0, strSum.getBytes().length);
                        outStream.write(Double.toString(sum).getBytes(), 0, Double.toString(sum).getBytes().length);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

/*
                rOd.closeFile();
                */
                    Toast.makeText(MainActivity.this, "saved to " + filepath, Toast.LENGTH_SHORT).show();
                }//end else
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void ShowDialogTipUserRequestPermission() {
        new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("需要获取存储空间\n否则将无法存储记录")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    //sxs

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限
    private void showDialogTipUserGoToAppSettting() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许list-sumer使用存储权限来保存数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面 101
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权 DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    //sxs

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
//			ViewHolder holder=null;

//			if(null == convertView){
//				holder=new ViewHolder();
            convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, null);


//            ScrollView sv = (ScrollView)convertView.findViewById(R.id.scrollView1);
//            sv.fullScroll(ScrollView.FOCUS_DOWN);

            final EditText editText1 = (EditText) convertView.findViewById(R.id.editText1);//序号
            final EditText editText2 = (EditText) convertView.findViewById(R.id.editText2);//输入值

            editText2.requestFocus();

            Button btnMinus = (Button) convertView.findViewById(R.id.buttonMinus);//删除行按钮

//				convertView.setTag(holder);
//			}else{
//				holder=(ViewHolder) convertView.getTag();
//			}
            final goods model = list.get(position);
            //int pos2 = position -1;
            //if (pos2 < 0)
            //    pos2 = 0;
            //final goods model2 = list.get(pos2);

            //Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
            String str = model.getName();
            //String str2 = model2.getName2();
            //String str2 = editText2.getText().toString();
            // Toast.makeText(MainActivity.this, str2, Toast.LENGTH_SHORT).show();
            if (str.equalsIgnoreCase("")) {
                editText1.setHint("123");
            } else {
                editText1.setText(str);
                if (gpreValue[position] != 0.0)
                    editText2.setText(Double.toString(gpreValue[position]));
                //if (gnum % 2 == 0)
                //editText2.setBackgroundColor(0x660066ff);
            }
            /*
            editText1.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                    list.get(position).setName(s.toString());
                }
            });
            */

            //删除行按钮点击监听事件
            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    //Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();

                    for (int j = list.size() - 1; j >= 0; j--) {
                        list.get(j).setName(Integer.toString(j));
                    }
                    gnum--;

                    adapter.notifyDataSetChanged();//重要，否则删除后不刷新界面，继续删除容易崩溃

                    sum -= gpreValue[position];

                    //前移数值
                    for (int i = position; i < 99; i++) {
                        gpreValue[i] = gpreValue[i + 1];
                    }
                    gpreValue[99] = 0;


                    //Toast.makeText(MainActivity.this, Double.toString(sum), Toast.LENGTH_SHORT).show();
                    TextView tvRes = (TextView) findViewById(R.id.result);
                    tvRes.setTextColor(0xff0066ff);
                    //tvRes.setText("SUM  " + Double.toString(sum));

                    BigDecimal bd1 = new BigDecimal(sum);
                    bd1 = bd1.setScale(2, BigDecimal.ROUND_HALF_UP);
                    //BigDecimal resOut = new BigDecimal(sum);
                    tvRes.setText("SUM  " + bd1);

                    //Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
                }
            });

            editText2.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub

                    String tmp = editText2.getText().toString();
                    if (tmp == null || tmp.length() <= 0)
                        gpreValue[position] = 0.0;
                    else
                        gpreValue[position] = Double.parseDouble(tmp);

                    sum = 0;
                    for (int i = 0; i < 100; i++)
                        sum += gpreValue[i];
                    //Toast.makeText(MainActivity.this, Double.toString(sum), Toast.LENGTH_SHORT).show();
                    TextView tvRes = (TextView) findViewById(R.id.result);
                    tvRes.setTextColor(0xff0066ff);
                    //tvRes.setText("SUM  " + Double.toString(sum));
                    BigDecimal bd = new BigDecimal(sum);
                    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    //BigDecimal resOut = new BigDecimal(sum);
                    tvRes.setText("SUM  " + bd);
                    //sum = 0;

                    //list.get(position).setName2(tmp);
                    //Toast.makeText(MainActivity.this, tmp, Toast.LENGTH_SHORT).show();
                }
            });


            return convertView;
        }

    }

    public static class ViewHolder {
        private EditText editText1;
        //private EditText editText2;
    }

}


