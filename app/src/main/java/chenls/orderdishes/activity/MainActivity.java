package chenls.orderdishes.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import chenls.orderdishes.R;
import chenls.orderdishes.bean.MyUser;
import chenls.orderdishes.content.DishContent;
import chenls.orderdishes.fragment.CategoryFragment;
import chenls.orderdishes.fragment.DishFragment;
import cn.bmob.v3.datatype.BmobFile;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , DishFragment.OnListFragmentInteractionListener, CategoryFragment.OnListFragmentInteractionListener {

    private static final int SET_INFORMATION = 1;
    public static final String PIC = "pic";
    private TextView tv_name;
    private TextView tv_phone_num;
    private ImageView iv_pic;
    private Fragment from;
    private DishFragment dishFragment;
    private CategoryFragment categoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Intent intent = new Intent(this, OrderDishActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
        //设置抽屉DrawerLayout
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        Intent intent1 = getIntent();
        if (intent1.getBooleanExtra(WelcomeActivity.IS_FIRST_OPEN, false)) {
            drawer.openDrawer(GravityCompat.START); //如果第一次打开，则打开抽屉
        }
        toggle.syncState();
        //设置导航栏NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        iv_pic = (ImageView) headerView.findViewById(R.id.iv_pic);
        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, SetInformationActivity.class);
                startActivityForResult(intent2, SET_INFORMATION);
                drawer.closeDrawer(GravityCompat.START);//关闭抽屉
            }
        });
        tv_name = (TextView) headerView.findViewById(R.id.tv_name);
        tv_phone_num = (TextView) headerView.findViewById(R.id.tv_phone_num);
        changeInformation(null);
        final ImageView iv_logout = (ImageView) headerView.findViewById(R.id.iv_logout);
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出登录
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.logout));
                builder.setMessage(getString(R.string.sure_logout));
                builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyUser.logOut(MainActivity.this);
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        });
        dishFragment = DishFragment.newInstance();
        from = dishFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.linear_layout, dishFragment);
        fragmentTransaction.commit();
    }

    private void changeInformation(Bitmap bitmap) {
        if (bitmap != null) {
            iv_pic.setImageBitmap(bitmap);
        }
        MyUser myUser = MyUser.getCurrentUser(MainActivity.this, MyUser.class);
        BmobFile pic = myUser.getPic();
        if (pic != null) {
            String url = pic.getFileUrl(MainActivity.this);
            setFaceImage(url);
        }
        tv_name.setText((String) MyUser.getObjectByKey(this, "username"));
        tv_phone_num.setText((String) MyUser.getObjectByKey(this, "mobilePhoneNumber"));
    }

    private void setFaceImage(String url) {
        Glide.with(MainActivity.this)
                .load(url)
                .asBitmap()
                .centerCrop()
                .into(iv_pic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SET_INFORMATION) {
                Bitmap photo = data.getParcelableExtra(PIC);
                changeInformation(photo);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_discover:
                switchContent(dishFragment);
                break;
            case R.id.nav_dish:
                if (categoryFragment == null) {
                    categoryFragment = CategoryFragment.newInstance();
                }
                switchContent(categoryFragment);
                break;
            case R.id.nav_order:
                break;
            case R.id.nav_setting:
                break;
            case R.id.nav_about:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void switchContent(Fragment to) {
        if (from != to) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (!to.isAdded()) {    // 先判断是否被add过
                // 隐藏当前的fragment，add下一个到Activity中
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .hide(from).add(R.id.linear_layout, to).commit();
            } else {
                // 隐藏当前的fragment，显示下一个
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .hide(from).show(to).commit();
            }
            from = to;
        }
    }

    @Override
    public void onDishListFragmentClick(DishContent.DishItem item, String num) {

    }

    @Override
    public void onDishListButtonClick(int pint, int num, int price, String name, int position, String image) {

    }

    @Override
    public void onDishListScroll(int index) {

    }

    @Override
    public void onCategoryListFragmentClick(int category_position, int dish_position) {

    }
}
