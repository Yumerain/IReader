package com.will.ireader.page;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toolbar;

import androidx.annotation.Nullable;

import com.will.ireader.R;
import com.will.ireader.base.BaseActivity;
import com.will.ireader.book.Book;
import com.will.ireader.book_list.MainActivity;
import com.will.ireader.common.SPHelper;
import com.will.ireader.page.view.PageView;
import com.will.ireader.page.printer.Printer;

/**
 * created  by will on 2019/7/5 15:43
 */
public class PageActivity extends BaseActivity {

    public static String PARAM_BOOK = "book";

    private View menu;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        //Log.e("book",new Gson().toJson(book));


    }

    private void initialize(){
        String displayType = SPHelper.getInstance().getDisplayType();
        if(displayType != null && displayType.equals(SPHelper.DISPLAY_TYPE_NOTCHED)){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_page);
        menu = findViewById(R.id.page_menu);
        menu.setAlpha(0);
        PageView page = findViewById(R.id.page_view);
        Book book = MainActivity.CURRENT_BOOK;
        book.initialize();
        page.setPrinter(new Printer(book));
        page.setOnMenuClickListener(isShowing -> menu.animate().alpha(isShowing ? 1 : 0)
        );
    }

}
