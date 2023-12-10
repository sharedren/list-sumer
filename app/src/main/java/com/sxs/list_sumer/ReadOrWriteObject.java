package com.sxs.list_sumer;

import android.widget.Toast;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ReadOrWriteObject {

    private FileInputStream fileIns = null;//文件输入流
    private FileOutputStream fileOts = null;//文件输出流
    private ObjectInputStream objectIns = null;//对象输入流
    private ObjectOutputStream objectOts = null;//对象输出流
    private String fileName = null;//待处理的文件名
    public static Boolean FileRead = true;
    public static Boolean FileWrite = false;

    /**
     * @param fileName:文件名
     */
    public ReadOrWriteObject(String fileName) {
        this.fileName = fileName;
    }

    public Boolean openFile(Boolean bool) {

        if (bool)//if bool is true,the file is read.
        {
            try {
                this.fileIns = new FileInputStream(this.fileName);
                this.objectIns = new ObjectInputStream(fileIns);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.fileOts = new FileOutputStream(this.fileName);
                this.objectOts = new ObjectOutputStream(this.fileOts);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 关闭文件流，对象流
     *
     * @return 成功关闭返回true
     */
    public Boolean closeFile() {

        if (null != this.fileIns) {
            try {
                this.objectIns.close();
                this.fileIns.close();
                objectIns = null;
                fileIns = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != this.fileOts) {
            try {
                this.objectOts.close();
                this.fileOts.close();
                objectOts = null;
                fileOts = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 刷新缓冲区
     */
    public void flush() {
        try {
            this.objectOts.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param d 需要写入文件的double数据
     * @return 写入成功则返回true，否则返回false
     */
    public Boolean writeDouble(double d) {
        if (this.objectOts != null) {
            try {
                this.objectOts.writeDouble(d);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 读取double型数据
     *
     * @return 返回无穷小为false
     */
    public double readDouble() {
        double d = 0;
        if (this.objectIns != null) {
            try {
                d = this.objectIns.readDouble();//读取到文件末尾，被EOFException捕获

            } catch (EOFException e) {
                this.closeFile();
                return Double.MIN_NORMAL;
            } catch (IOException e) {
                this.closeFile();
                e.printStackTrace();
            }
            return d;
        } else {
            return Double.MIN_NORMAL;
        }
    }
}