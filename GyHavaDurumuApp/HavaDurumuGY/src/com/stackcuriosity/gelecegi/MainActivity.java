package com.stackcuriosity.gelecegi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	// private static String
	// URI="http://api.openweathermap.org/data/2.5/weather?q=istanbul&units=metric";
	private static String URI = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
	private static String URIEK = "&units=metric&cnt=4";
	private JSONObject json;
	private TextView textViewIlGun, textViewIlGun2, textViewIkiciGun,
			textViewIkiciGun2, textViewUcuncuGun, textViewUcuncuGun2,
			textViewDorduncuGun, textViewDorduncuGun2, textViewSehirIsmi,
			text_gun, text_gun1, text_gun2, text_gun3;
	private String sicaklik;
	private int iconJson;
	private ImageView imageViewIlkGun, imageViewIkinciGun, imageViewUcuncuGun,
			imageViewDorduncuGun;
	private String[] jSonDizi;
	private RelativeLayout rl_layout;
	private static int background;
	View view, viewNoSplash;
	Button slideHandleButton;
	SlidingDrawer slidingDrawer;
	private String name;
	private Bitmap arkaplanResim;
	TextView splashText;
	RelativeLayout rl_splash;
	private Bundle extras = null;
	public static final int EMPTY_MESSAGE_WHAT = 0x001;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();

		try {
			Log.e("zms", "zms intent: 1 ");

			extras = getIntent().getExtras();
			String j = extras.getString("sehir");
			savePreferences("sehir", j);

			// SharedPreferences.Editor editor =
			// getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
			// editor.putString("sehir",j );
			Log.e("zms", "zms intent: " + j);
		} catch (Exception e) {
			// TODO: handle exception
		}
		// MY_PREFS_NAME - a static String variable like:
		// public static final String MY_PREFS_NAME = "MyPrefsFile";

		text_gun = (TextView) findViewById(R.id.text_isim_gun);
		text_gun1 = (TextView) findViewById(R.id.ikinci_gun_isim);
		text_gun2 = (TextView) findViewById(R.id.uc_gun_isim);
		text_gun3 = (TextView) findViewById(R.id.dort_gun_isim);
		rl_layout = (RelativeLayout) findViewById(R.id.relative_layout);
		// ilk Gun
		textViewIlGun = (TextView) findViewById(R.id.sicaklik);
		textViewIlGun2 = (TextView) findViewById(R.id.hava_durumu);
		imageViewIlkGun = (ImageView) findViewById(R.id.ana_icon);
		// 2. Gun
		textViewIkiciGun = (TextView) findViewById(R.id.ikinci_gun);
		textViewIkiciGun2 = (TextView) findViewById(R.id.ikinci_gun2);
		imageViewIkinciGun = (ImageView) findViewById(R.id.image_view_ikinci_gun);
		// 3. Gun
		textViewUcuncuGun = (TextView) findViewById(R.id.ucuncu_gun);
		textViewUcuncuGun2 = (TextView) findViewById(R.id.ucuncu_gun2);
		imageViewUcuncuGun = (ImageView) findViewById(R.id.image_view_ucuncu_gun);
		// 4. Gun
		textViewDorduncuGun = (TextView) findViewById(R.id.dorduncu_gun);
		textViewDorduncuGun2 = (TextView) findViewById(R.id.dorduncu_gun2);
		imageViewDorduncuGun = (ImageView) findViewById(R.id.image_view_dorduncu_gun);
		textViewSehirIsmi = (TextView) findViewById(R.id.hava_sehir);

		new havaAsyncTask().execute();
		// animasyon
		new Timer().schedule(new ExeTimerTask(), 0, 3000);

		// butn handle
		slideHandleButton = (Button) findViewById(R.id.handle);
		slidingDrawer = (SlidingDrawer) findViewById(R.id.bottom);

		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				slideHandleButton
						.setBackgroundResource(R.drawable.beyaz_ac_ters);
			}
		});

		slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				slideHandleButton.setBackgroundResource(R.drawable.beyaz_ac);
			}
		});
		Log.i("zms", "zms " + background);

	}

	private void startAnimation() {
		// TODO Auto-generated method stub

		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

		findViewById(R.id.handle).startAnimation(shake);

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			startAnimation();
		}
	};

	private class ExeTimerTask extends TimerTask {
		@Override
		public void run() {
			// we don't really use the message 'what' but we have to specify
			// something.
			mHandler.sendEmptyMessage(EMPTY_MESSAGE_WHAT);
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
		if (id == R.id.action_settings) {
			Bundle extras = new Bundle();
			boolean sehirSec = true;
			extras.putBoolean("sehirSec", sehirSec);
			Intent i = new Intent(MainActivity.this, KarsilamaEkran.class);
			i.putExtras(extras);
			startActivity(i);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class havaAsyncTask extends AsyncTask<String[], String, String[]> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setMessage("Yükleniyor...");
			progressDialog.show();
		}

		@Override
		protected String[] doInBackground(String[]... params) {
			String jsonParse = JSONParser.getJSONFromUrl(URI
					+ loadSavedPreferences() + URIEK);
			try {
				jSonDizi = new String[19];
				JSONObject jsonObject = new JSONObject(jsonParse);
				// ilk Gun
				sifirGun(jsonObject);
				// 2.Gun
				ikinciGun(jsonObject);
				// 3.Gun
				ucuncuGun(jsonObject);
				// 4.Gun
				dorduncuGun(jsonObject);

			} catch (JSONException e) {
				e.printStackTrace();

			}
			return jSonDizi;
		}

		@Override
		protected void onPostExecute(String[] s) {
			super.onPostExecute(s);
			// ilk Gun

			textViewIlGun.setText(s[0].toLowerCase() + "\u2103");
			textViewIlGun2.setText(s[1].toLowerCase());
			boolean sifirinciGunMu = true;
			setIconJson(s[2].toLowerCase(), sifirinciGunMu);
			textViewSehirIsmi.setText(s[13] + "\n" + s[14]);
			text_gun.setText(s[15]);

			// 2.Gun
			textViewIkiciGun.setText(s[3].toLowerCase() + "\u2103");
			textViewIkiciGun2.setText(s[4].toLowerCase());
			text_gun1.setText(s[16]);
			// 3.Gun
			textViewUcuncuGun.setText(s[6].toLowerCase() + "\u2103");
			textViewUcuncuGun2.setText(s[7].toLowerCase());
			text_gun2.setText(s[17]);
			// 4.Gun
			textViewDorduncuGun.setText(s[9].toLowerCase() + "\u2103");
			textViewDorduncuGun2.setText(s[10].toLowerCase());
			boolean ikinciGun = false;
			setIconJson(s[5], ikinciGun);
			
			Bitmap img = BitmapFactory.decodeResource(getResources(),
					getIconJson());
			text_gun3.setText(s[18]);
			// ilk Gun
			imageViewIlkGun.setImageBitmap(img);
			// 2.Gun
			Bitmap img2 = BitmapFactory.decodeResource(getResources(),
					getIconJson());
			imageViewIkinciGun.setImageBitmap(img2);

			// 3.Gun
			boolean ucuncuGun = false;
			setIconJson(s[8], ucuncuGun);
			Bitmap img3 = BitmapFactory.decodeResource(getResources(),
					getIconJson());
			imageViewUcuncuGun.setImageBitmap(img3);

			// 4.Gun
			boolean dorduncuGun = false;
			setIconJson(s[11], dorduncuGun);
			Bitmap img4 = BitmapFactory.decodeResource(getResources(),
					getIconJson());
			imageViewDorduncuGun.setImageBitmap(img4);
			progressDialog.dismiss();
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			progressDialog.dismiss();

		}

	}

	private String[] sifirGun(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		Log.i("zms", "zms hata sifirGun 1");

		JSONArray listArray = jsonObject.getJSONArray("list");
		JSONObject ilkObje = listArray.getJSONObject(0);
		JSONObject tempObject = ilkObje.getJSONObject("temp");
		jSonDizi[0] = tempObject.getString("day");
		Log.i("zms", "zms hata sifirGun 2");

		// hava Durumu
		JSONArray weatherArray = ilkObje.getJSONArray("weather");
		Log.i("zms", "zms hata sifirGun 3");
		JSONObject icoN = weatherArray.getJSONObject(0);
		jSonDizi[1] = icoN.getString("main");
		jSonDizi[2] = icoN.getString("icon");
		jSonDizi[12] = icoN.getString("description");
		// boolean sifirinciGunMu=true;
		// setIconJson(jSonDizi[2],sifirinciGunMu);
		Log.i("zms", "zms hata sifirGun 4");

		// Ýl adý
		JSONObject havaObject = jsonObject.getJSONObject("city");

		jSonDizi[13] = havaObject.getString("name");
		// tarih
		long dt = ilkObje.getLong("dt");
		Log.e("zms", "zms dt: " + dt);
		Date date = new Date(dt * 1000L); // *1000 is to convert seconds to
											// milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // the format
																	// of your
																	// date
		// give a timezone reference for formating (see comment at the bottom
		String formattedDate = sdf.format(date);
		jSonDizi[14] = formattedDate;

		SimpleDateFormat gun = new SimpleDateFormat("EEEE");
		String formattedDate2 = gun.format(date);
		jSonDizi[15] = formattedDate2;

		// 2-3-4 üncü Gunler

		return jSonDizi;

	}

	private String[] ikinciGun(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		Log.i("zms", "zms hata ikinciGun 1");

		JSONArray listArray = jsonObject.getJSONArray("list");
		JSONObject ilkObje = listArray.getJSONObject(1);
		JSONObject tempObject = ilkObje.getJSONObject("temp");
		jSonDizi[3] = tempObject.getString("day");
		Log.i("zms", "zms hata ikinciGun 2");

		// hava Durumu
		JSONArray weatherArray = ilkObje.getJSONArray("weather");
		Log.i("zms", "zms hata ikinciGun 3");
		JSONObject icoN = weatherArray.getJSONObject(0);
		jSonDizi[4] = icoN.getString("main");
		jSonDizi[5] = icoN.getString("icon");
//		boolean ikinciGun = false;
//		setIconJson(jSonDizi[5], ikinciGun);
		Log.i("zms", "zms hata ikinciGun 4");
		long dt = ilkObje.getLong("dt");
		Date date = new Date(dt * 1000L); // *1000 is to convert seconds to
											// milliseconds
		SimpleDateFormat gun = new SimpleDateFormat("EEEE");
		String formattedDate2 = gun.format(date);
		jSonDizi[16] = formattedDate2;

		return jSonDizi;
	}

	private String[] ucuncuGun(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		Log.i("zms", "zms hata ucuncuGun 1");

		JSONArray listArray = jsonObject.getJSONArray("list");
		JSONObject ilkObje = listArray.getJSONObject(2);
		JSONObject tempObject = ilkObje.getJSONObject("temp");
		jSonDizi[6] = tempObject.getString("day");
		Log.i("zms", "zms hata ucuncuGun 2");

		// hava Durumu
		JSONArray weatherArray = ilkObje.getJSONArray("weather");
		Log.i("zms", "zms hata ucuncuGun 3");
		JSONObject icoN = weatherArray.getJSONObject(0);
		jSonDizi[7] = icoN.getString("main");
		jSonDizi[8] = icoN.getString("icon");
//		boolean ucuncuGun = false;
//		setIconJson(jSonDizi[8], ucuncuGun);
		Log.i("zms", "zms hata ucuncuGun 4");
		long dt = ilkObje.getLong("dt");
		Date date = new Date(dt * 1000L); // *1000 is to convert seconds to
											// milliseconds
		SimpleDateFormat gun = new SimpleDateFormat("EEEE");
		String formattedDate2 = gun.format(date);
		jSonDizi[17] = formattedDate2;
		return jSonDizi;
	}

	private String[] dorduncuGun(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		Log.i("zms", "zms hata dorduncuGun 1");

		JSONArray listArray = jsonObject.getJSONArray("list");
		JSONObject ilkObje = listArray.getJSONObject(3);
		JSONObject tempObject = ilkObje.getJSONObject("temp");
		jSonDizi[9] = tempObject.getString("day");
		Log.i("zms", "zms hata dorduncuGun 2");

		// hava Durumu
		JSONArray weatherArray = ilkObje.getJSONArray("weather");
		Log.i("zms", "zms hata dorduncuGun 3");
		JSONObject icoN = weatherArray.getJSONObject(0);
		jSonDizi[10] = icoN.getString("main");
		jSonDizi[11] = icoN.getString("icon");
//		boolean dorduncuGun = false;
//		setIconJson(jSonDizi[11], dorduncuGun);
		Log.i("zms", "zms hata dorduncuGun 4");
		long dt = ilkObje.getLong("dt");
		Date date = new Date(dt * 1000L); // *1000 is to convert seconds to
											// milliseconds
		SimpleDateFormat gun = new SimpleDateFormat("EEEE");
		String formattedDate2 = gun.format(date);
		jSonDizi[18] = formattedDate2;

		return jSonDizi;
	}

	public int getIconJson() {

		return iconJson;
	}

	public void setIconJson(String iconJson, boolean sifirinciGunMu) {
		Log.e("zms", "zms setIconJson sifirinciGun: " + sifirinciGunMu);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		
		if (iconJson.equals("01d")) {
			this.iconJson = R.drawable.sky_is_clear_day;

			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.sky_is_clear);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("02d")) {

			this.iconJson = R.drawable.few_clouds_day;

			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.few_clouds);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("03d")) {

			this.iconJson = R.drawable.scattered_clouds_day;

			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.broken_clouds);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("04d")) {

			this.iconJson = R.drawable.broken_clouds_day;

			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.broken_clouds);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("09d")) {

			this.iconJson = R.drawable.shower_rain_day;

			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.rain_street);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("10d")) {

			this.iconJson = R.drawable.rain_day;

			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.rain_street);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("11d")) {

			this.iconJson = R.drawable.thunderstorm_day;

			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.thunderstorm_uc);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("13d")) {

			this.iconJson = R.drawable.snow_day;

			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.snow);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);
			}
		} else if (iconJson.equals("50d")) {
			this.iconJson = R.drawable.mist_day;

			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.mist_iki);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		}
		// Geceler

		else if (iconJson.equals("01n")) {
			this.iconJson = R.drawable.sky_is_clear_night;
			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.sky_is_clear);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("02n")) {
			this.iconJson = R.drawable.few_clouds_night;
			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.few_clouds);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("03n")) {
			this.iconJson = R.drawable.scattered_clouds_night;
			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.broken_clouds);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("04n")) {
			this.iconJson = R.drawable.broken_clouds_night;
			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.broken_clouds);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("09n")) {
			this.iconJson = R.drawable.shower_rain_night;
			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.rain_street);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("10n")) {
			this.iconJson = R.drawable.rain_night;
			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.rain_street);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("11n")) {
			this.iconJson = R.drawable.thunderstorm_night;
			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.thunderstorm_uc);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		} else if (iconJson.equals("13n")) {
			this.iconJson = R.drawable.snow_night;
			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.snow);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);
			}
		} else if (iconJson.equals("50n")) {
			this.iconJson = R.drawable.mist_night;
			if (sifirinciGunMu == true) {
				arkaplanResim = BitmapFactory.decodeResource(getResources(),
						R.drawable.mist_iki);
				Drawable d = new BitmapDrawable(getResources(),
						Bitmap.createScaledBitmap(arkaplanResim, width, height,
								true));
				rl_layout.setBackgroundDrawable(d);

			}
		}
	}

	private String loadSavedPreferences() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		String url = sp.getString("sehir", "istanbul");
		return url;

	}

	private void savePreferences(String key, String value) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

}
