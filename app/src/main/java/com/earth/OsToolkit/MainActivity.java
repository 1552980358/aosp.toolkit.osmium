package com.earth.OsToolkit;

import android.content.DialogInterface;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import com.earth.OsToolkit.Fragment.*;
import com.earth.OsToolkit.Fragment.Dialog.UpdateDialogFragment;
import com.earth.OsToolkit.Working.BaseClass.*;

import java.lang.Process;
import java.util.*;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
		checkUpdate();
	}

	public void initUI() {
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.main_fragment, new MainFragment()).commit();

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	public void checkUpdate() {
		Toast.makeText(this, getString(R.string.update_checking), Toast.LENGTH_SHORT).show();
		CheckUpdate checkUpdate = new CheckUpdate();
		checkUpdate.checkUpdate();

		while (!checkUpdate.complete) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
				}
			}, 50);
		}

		if (checkUpdate.complete && !checkUpdate.getVersion().equals(Checking.getVersionName(this))) {
			UpdateDialogFragment updateDialogFragment = new UpdateDialogFragment();
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			updateDialogFragment.setVerision(checkUpdate.getVersion());
			updateDialogFragment.setDate(checkUpdate.getDate());
			updateDialogFragment.setChangelogEng(checkUpdate.getChangelogEng());
			updateDialogFragment.setChangelogCn(checkUpdate.getChangelogCn());
			updateDialogFragment.show(fragmentTransaction, "updateDialogFragment");
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
	@SuppressWarnings("all")
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		Process process;
		switch (id) {
			case R.id.menu_exit:
				ExitApplication.exit();
				return true;

			case R.id.menu_shell:
				ExitApplication.shellKill(this);
				return true;

			case R.id.menu_killProcessPID:
				ExitApplication.killProcessPID();
				return true;

			case R.id.menu_null:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.menu_null_title)
						.setMessage(R.string.menu_null_msg)
						.setPositiveButton(R.string.cont, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								TextView textView = findViewById(R.id.script_txt);
								textView.setText("");
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();
				return true;

			default:
				return super.onOptionsItemSelected(item);

		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
		Fragment fragment = new MainFragment();
		int title = R.string.app_name;
		switch (id) {
			case R.id.nav_main:
				fragment = new MainFragment();
				break;
			case R.id.nav_charging:
				title = R.string.nav_charging;
				fragment = new ChargingFragment();
				break;
			case R.id.nav_about:
				title = R.string.nav_about;
				fragment = new AboutFragment();
				break;
			case R.id.nav_deviceinfo:
				title = R.string.nav_deviceinfo;
				fragment = new DeviceInfoFragment();
				break;
			case R.id.nav_cores:
				title = R.string.nav_processor;
				fragment = new CoresFragment();
				break;
			case R.id.nav_applyyc:
				title = R.string.nav_yc;
				fragment = new ApplyYCFragment();
				break;
		}

		toolbar.setTitle(title);
		ft.replace(R.id.main_fragment, fragment).commit();

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

}
