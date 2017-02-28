package me.iwf.photopicker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import me.iwf.photopicker.fragment.ImagePagerFragment;

/**
 * Created by donglua on 15/6/24.
 */
public class PhotoPagerActivity extends AppCompatActivity {

  private ImagePagerFragment pagerFragment;
  public final static String EXTRA_PHOTO_PATH = "photoPath";
  private ActionBar actionBar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_photo_pager);

    String paths = getIntent().getStringExtra(EXTRA_PHOTO_PATH);

    pagerFragment =
        (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
    pagerFragment.setPhotos(paths);

    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    actionBar = getSupportActionBar();

    actionBar.setDisplayHomeAsUpEnabled(true);
    updateActionBarTitle();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      actionBar.setElevation(25);
    }
  }


  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_preview, menu);
    return true;
  }


  @Override public void onBackPressed() {

    Intent intent = new Intent();
    intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, pagerFragment.getPaths());
    setResult(RESULT_OK, intent);
    finish();

    super.onBackPressed();
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void updateActionBarTitle() {

  }
}
