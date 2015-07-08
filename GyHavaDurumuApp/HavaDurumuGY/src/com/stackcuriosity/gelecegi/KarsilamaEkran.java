package com.stackcuriosity.gelecegi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class KarsilamaEkran extends Activity implements AnimationListener {
	TextView karsilama, karsilama_edt_anim;
	EditText edt_sehir;
	// Shared Preferences
	private String filename = "MySharedPref";
	private SharedPreferences aData = null;
	private static Boolean isThisFirstTime;
	Animation animFadein, animFadeout;
	AutoCompleteTextView textView;
	private static final String[] COUNTRIES = new String[] { "Adana",
			"Adiyaman", "Afyonkarahisar", "Agri", "Amasya", "Ankara",
			"Antalya", "Artvin", "Aydin", "Balikesir", "Bilecik", "Bingol",
			"Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankiri",
			"Çorum", "Denizli", "Diyarbakir", "Edirne", "Elazig", "Erzincan",
			"Erzurum", "Eskisehir", "Gaziantep", "Giresun", "Gumushane",
			"Hakkari", "Hatay", "Isparta", "Mersin", "Ýstanbul", "Ýzmir",
			"Kars", "Kastamonu", "Kayseri", "Kirklareli", "Kirsehir",
			"Kocaeli", "Konya", "Kutahya", "Malatya", "Manisa",
			"Kahramanmaras", "Mardin", "Mugla", "Mus", "Nevsehir", "Nigde",
			"Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas",
			"Tekirdag", "Tokat", "Trabzon", "Tunceli", "Sanliurfa", "Usak",
			"Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
			"Kirikkale", "Batman", "sirnak", "Bartin", "Ardahan", "Igdir",
			"Yalova", "Karabuk", "Kilis", "Osmaniye", "Duzce" };
	private ProgressDialog progressDialog;
	private Handler mHandler = new Handler();
	Button btn_next;
	ArrayAdapter<String> adapter;
	private Bundle extras = null;
	private static boolean j = false;
	private static String URI = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
	private static String URIEK = "&units=metric&cnt=4";
	String sehirKntrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.karsilama_ekran);
		btn_next = (Button) findViewById(R.id.button1);
		karsilama = (TextView) findViewById(R.id.splash_text);
		karsilama_edt_anim = (TextView) findViewById(R.id.splash_text);

		// dt_sehir=(EditText)findViewById(R.id.sehir_giris_edt);
		getActionBar().hide();
		animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.fade_in);
		animFadeout = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.fade_out);

		extras = getIntent().getExtras();
		if (extras != null) {
			j = extras.getBoolean("sehirSec");
		} else {
			j = false;
		}

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, COUNTRIES);
		textView = (AutoCompleteTextView) findViewById(R.id.sehir_giris_edt);
		textView.setAdapter(adapter);
		animFadein.setAnimationListener(this);
		animFadeout.setAnimationListener(this);
		// set animation listener

		// sharedprefrences

		aData = getSharedPreferences(filename, MODE_PRIVATE);
		isThisFirstTime = aData.getBoolean("hasRun", false);
		if (!isThisFirstTime) { // ilk kez giriliyorsa
			CountDownTimer countDown = new CountDown(10000, 5000);
			countDown.start();
			// animation1();
			// animation2();

			// karsilama.setText("Merhaba Geleceði Yazanlar Ekibi.\nHoþgeldiniz...");

			btn_next.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String shir = String.valueOf(textView.getText())
							.toLowerCase();

					Log.i("zms", "zms textView: " + shir);
					savePreferences("sehir", shir);
					if (shir != null) {
						new kontrolAsync().execute();

					} else {
						Toast.makeText(getApplicationContext(),
								"Sehir Giriþini Boþ Geçmeyiniz.",
								Toast.LENGTH_LONG).show();
					}
				}
			});

			/*
			 * Su andan itibaren hasRun' in degerini true yapalim ki program
			 * tekrar calisinca bu kod bloguna girilmesin. Bunun icin aData' yi
			 * duzenlememiz gerekiyor.Bu islemi yapmak icin asagidakileri
			 * yapmaliyiz.
			 */

		} else if (j == true) {
			karsilama.setText(getString(R.string.text_sehir_giriniz));
			btn_next.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					String shir = String.valueOf(textView.getText())
							.toLowerCase();
					Bundle extras = new Bundle();
					extras.putString("sehir", shir);
					Log.i("zms", "zms textView: " + shir);
					if (shir != null) {
						Intent i = new Intent(KarsilamaEkran.this,
								MainActivity.class);
						i.putExtras(extras);
						startActivity(i);
						finish();
					}
				}
			});

		} else {
			Intent i = new Intent(KarsilamaEkran.this, MainActivity.class);
			startActivity(i);
			finish();
		}

	}


	public void animation1() {
		karsilama.setText("Merhaba Geleceði Yazanlar Ekibi.\nHoþgeldiniz...");
		// karsilama.startAnimation(animFadein);
		// karsilama.startAnimation(animFadeout);
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
		fadeIn.setDuration(5000);

		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
		fadeOut.setStartOffset(5000);
		fadeOut.setDuration(5000);

		AnimationSet animation = new AnimationSet(true); // change to false
		animation.addAnimation(fadeIn);
		animation.addAnimation(fadeOut);
		karsilama.setAnimation(animation);

	}

	public void animation2() {
		// TODO Auto-generated method stub
		karsilama_edt_anim
				.setText("Lütfen Hava Durumunu Öðrenmek Ýstediðiniz Þehri Giriniz...");
		// karsilama.startAnimation(animFadein);
		// karsilama.startAnimation(animFadeout);
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
		fadeIn.setDuration(10000);

		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
		fadeOut.setStartOffset(15000);
		fadeOut.setDuration(5000);

		AnimationSet animation = new AnimationSet(true); // change to false
		animation.addAnimation(fadeIn);
		animation.addAnimation(fadeOut);
		karsilama_edt_anim.setAnimation(animation);
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	public class CountDown extends CountDownTimer {

		public CountDown(int millisInFuture, int countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub

		}

		@Override
		public void onTick(long millisUntilFinished) {

			animation1();
		}

		@Override
		public void onFinish() {
			animation2();
			// countDown.setText("Süre Doldu");
		}
	}

	public class kontrolAsync extends AsyncTask<String[], Integer, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(KarsilamaEkran.this);
			progressDialog.setMessage("Yükleniyor...");
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String[]... arg0) {
			String jsonParse = JSONParser.getJSONFromUrl(URI
					+ loadSavedPreferences() + URIEK);
			Log.i("zms", "zms ÞehirKontrol URL:" + jsonParse);

			try {
				// jSonDizi = new String[1];
				JSONObject jsonObject = new JSONObject(jsonParse);
				sehirKntrl = jsonObject.getString("cod");
				Log.i("zms", "zms ÞehirKontrol" + sehirKntrl);
			} catch (JSONException e) {
				e.printStackTrace();

			}
			return sehirKntrl;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (!result.equals("404")) {
				Bundle extras = new Bundle();
				extras.putString("sehir", loadSavedPreferences());
				Intent i = new Intent(KarsilamaEkran.this, MainActivity.class);
				i.putExtras(extras);
				startActivity(i);
				finish();
				SharedPreferences.Editor prefEditor = aData.edit();
				prefEditor.putBoolean("hasRun", true);
				prefEditor.commit(); // commit ile yaptigimiz degisiklikleri
										// onayladigimizi belirtiyoruz.
			} else {
				Toast.makeText(getApplicationContext(),
						"Böyle Bir Þehir Bulunamadý", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			progressDialog.dismiss();

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
