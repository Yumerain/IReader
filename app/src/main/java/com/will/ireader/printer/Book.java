package com.will.ireader.printer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.will.ireader.common.SPHelper;
import com.will.ireader.worker.AppWorker;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * created  by will on 2019/6/6 16:25
 */
@Entity
public class Book implements Serializable {



    @NonNull
    @PrimaryKey
    private String path;
    private String name;


    private int bookmark = -1;
    private int byteLength = -1;
    private String charset = "gbk";

    @Ignore
    private  RandomAccessFile randomFile;
    @Ignore
    private  MappedByteBuffer mappedFile;




    public Book(String name,String path){
        this.name = name;
        this.path = path;
    }


    public void initialize(){
        mappedFile = load();
        byteLength = mappedFile.capacity();
    }
    public void initialize(BookInitializeListener listener){

    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public int getByteLength() {
        check();
        return byteLength;
    }

    public void setByteLength(int byteLength) {
        this.byteLength = byteLength;
    }

    public String getCharset() {
        check();
        return charset;
    }
    public void setCharset(String charset) {
        this.charset = charset;
        SPHelper.getInstance().setBookCharset(getPath(),charset);
    }


    public int getBookmark() {
        check();
        return bookmark;
    }
    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
        SPHelper.getInstance().setBookmark(path, bookmark);
    }


    public MappedByteBuffer bytes() {
       check();
       return mappedFile;
    }

    private MappedByteBuffer load(long start, long end){
        if(mappedFile != null) {
            return  mappedFile;
        }
        File file = new File(path);
        start = Math.min(Math.max(start,0),file.length());
        end = Math.min(Math.max(end,0),file.length());

        try {
            randomFile = new RandomAccessFile(file, "r");
            mappedFile = randomFile.getChannel().map(FileChannel.MapMode.READ_ONLY,start,end);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("book","load error");
            return null;
        }
        return mappedFile;
    }

    private MappedByteBuffer load(){
        return  load(-1,Long.MAX_VALUE);
    }

    public void close() {
        mappedFile = null;
        if(randomFile == null ){
            return;
        }
        try{
            randomFile.close();
            randomFile = null;

        }catch (IOException i){
            i.printStackTrace();
            Log.e("book","close error");
        }
    }

    private void check(){
        if(bookmark == -1 ||byteLength == -1 || charset == null || mappedFile == null){
            throw (new RuntimeException("you must call initialize() before you invoke this method"));
        }
    }

    public void save(Context context){
        AppWorker.getInstance().getHandler().post(() -> AppDatabase.getInstance(context).bookDao().updateBook(this));
    }

    interface BookInitializeListener{
        void onFinish();
    }

}
