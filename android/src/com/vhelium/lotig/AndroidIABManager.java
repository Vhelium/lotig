package com.vhelium.lotig;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.hud.GemStoreHUD;
import com.vhelium.lotig.util.IabHelper;
import com.vhelium.lotig.util.IabResult;
import com.vhelium.lotig.util.Inventory;
import com.vhelium.lotig.util.Purchase;

public class AndroidIABManager implements IABManager, IEventListener
{
	static final String TAG = "AndroidIABManager";
	static final String SKU_GEMS = "gems";
	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 11011;
	private final char[] symbols = new char[36];
	
	CopyOnWriteArrayList<String> payloadQueue = new CopyOnWriteArrayList<String>();
	
	Activity context;
	IabHelper mHelper;
	
	GemStoreHUD hud;
	
	public AndroidIABManager(Activity context)
	{
		this.context = context;
		
		for(int idx = 0; idx < 10; ++idx)
			symbols[idx] = (char) ('0' + idx);
		for(int idx = 10; idx < 36; ++idx)
			symbols[idx] = (char) ('a' + idx - 10);
		
		//register listener:
		GameHelper.getPlatformResolver().addEventListener(this);
		
		String ermagerd = getErmagerd();
		Log.e("IABM", "KEY: " + ermagerd);
		mHelper = new IabHelper(context, ermagerd);
		mHelper.enableDebugLogging(true);//TODO: Debugging only
	}
	
	private String getErmagerd()
	{
		String efgrg = "MhLloaY1S85isybfZJ+eSK/MRnbDjX/sdQfoRh3Mq";
		String fsefsf = "LO5X1LRUG3KG0sSmwZTq/MCMcMoaL4JRUSCk/mjqEaT3BUtKlDUJ8YanBysLVJE9QIIgQh+9aSYxmJtUaNd5fuwuZLAw9YgjYbs8+Jfd8sP88D2aLH9b2";
		String sefesf = "P4TMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkOmMhLloaY1S85isybfZJ+eSK/MRnbDjX/sdQfoRh3MqNHc";
		String efssef = "3RdIIa/ZhYmY5HOZ0X9M4kKEHd05oXxn6lzDvvVCYNvf7CSquAwuJLrny8BtVOe5AyCCTrM3EE8fBlGJifnCbQIDAQABB5";
		String sefsefe = "U2DI9SHlCWZJLLHsp72VvlQPL34jG0OE1qMsSuCN2RtT6AC6sRQ4jgOqrttcZOet/YvAPwFS1gUrsqbJwPgoApHBidcdUp+G5GT";
		String er34se = "BgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkOmMhLloaY1S85isybfZJ+eSK/MRnbDjX/sdQfoUG3KG0sSmwZTq/MCMcMoaL4JRUSCk";
		
		return sefesf.substring(3, sefesf.length()) + fsefsf.substring(5, fsefsf.length()) + sefsefe.substring(0, sefsefe.length() - 2) + efssef.substring(0, efssef.length() - 2);
	}
	
	private void initialize()
	{
		if(!mHelper.isSetup())
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
			{
				@Override
				public void onIabSetupFinished(IabResult result)
				{
					Log.d(TAG, "Setup finished.");
					
					if(!result.isSuccess())
					{
						// Oh noes, there was a problem.
						complain("Problem setting up in-app billing: " + result);
						return;
					}
					
					// Have we been disposed of in the meantime? If so, quit.
					if(mHelper == null)
						return;
					
					// IAB is fully set up. Now, let's get an inventory of stuff we own.
					Log.d(TAG, "Setup successful. Querying inventory.");
					mHelper.queryInventoryAsync(mGotInventoryListener);
				}
			});
	}
	
	// Listener that's called when we finish querying the items we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener()
	{
		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inventory)
		{
			Log.d(TAG, "Query inventory finished.");
			
			// Have we been disposed of in the meantime? If so, quit.
			if(mHelper == null)
				return;
			
			// Is it a failure?
			if(result.isFailure())
			{
				complain("Failed to query inventory: " + result);
				return;
			}
			
			Log.d(TAG, "Query inventory was successful.");
			
			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */
			
			Purchase gem1Purchase = inventory.getPurchase(SKU_GEMS + GemStoreHUD.GEM_AMOUNT_50);
			if(gem1Purchase != null && verifyDeveloperPayload(gem1Purchase))
			{
				Log.d(TAG, "We have 50 gems. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_GEMS + GemStoreHUD.GEM_AMOUNT_50), mConsumeFinishedListener);
				return;
			}
			
			Purchase gem2Purchase = inventory.getPurchase(SKU_GEMS + GemStoreHUD.GEM_AMOUNT_100);
			if(gem2Purchase != null && verifyDeveloperPayload(gem2Purchase))
			{
				Log.d(TAG, "We have 100 gems. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_GEMS + GemStoreHUD.GEM_AMOUNT_100), mConsumeFinishedListener);
				return;
			}
			
			Purchase gem3Purchase = inventory.getPurchase(SKU_GEMS + GemStoreHUD.GEM_AMOUNT_250);
			if(gem3Purchase != null && verifyDeveloperPayload(gem3Purchase))
			{
				Log.d(TAG, "We have 250 gems. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_GEMS + GemStoreHUD.GEM_AMOUNT_250), mConsumeFinishedListener);
				return;
			}
			
			Purchase gem4Purchase = inventory.getPurchase(SKU_GEMS + GemStoreHUD.GEM_AMOUNT_750);
			if(gem4Purchase != null && verifyDeveloperPayload(gem4Purchase))
			{
				Log.d(TAG, "We have 750 gems. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_GEMS + GemStoreHUD.GEM_AMOUNT_750), mConsumeFinishedListener);
				return;
			}
			
			setWaitScreen(false);
			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}
	};
	
	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener()
	{
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase purchase)
		{
			Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
			
			// if we were disposed of in the meantime, quit.
			if(mHelper == null)
				return;
			
			if(result.isFailure())
			{
				complain("Error purchasing: " + result);
				setWaitScreen(false);
				return;
			}
			if(!verifyDeveloperPayload(purchase))
			{
				complain("Error purchasing. Authenticity verification failed.");
				setWaitScreen(false);
				return;
			}
			
			Log.d(TAG, "Purchase successful.");
			
			if(purchase.getSku().startsWith(SKU_GEMS))
			{
				Log.d(TAG, "Purchase is gems. Starting gem consumption.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
		}
	};
	
	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener()
	{
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result)
		{
			Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
			
			// if we were disposed of in the meantime, quit.
			if(mHelper == null)
				return;
			
			if(result.isSuccess())
			{
				if(purchase.getSku().startsWith(SKU_GEMS))
				{
					int gems = Integer.parseInt(purchase.getSku().substring(SKU_GEMS.length(), purchase.getSku().length()));
					// successfully consumed, so we apply the effects of the item in our
					// game world's logic, which in our case means filling the gas tank a bit
					Log.d(TAG, "Consumption successful. Provisioning.");
					giveGems(gems);
					alert("You bought " + gems + " gems! Congratz!");
				}
			}
			else
			{
				complain("Error while consuming: " + result);
			}
			setWaitScreen(false);
			Log.d(TAG, "End consumption flow.");
		}
	};
	
	@Override
	public void onShowUp(GemStoreHUD gemStoreHUD)
	{
		this.hud = gemStoreHUD;
		initialize();
	}
	
	@Override
	public void onGemBuyClicked(int amount)
	{
		Log.d(TAG, "Buy gems button clicked.");
		
		if(amount == GemStoreHUD.GEM_AMOUNT_50 || amount == GemStoreHUD.GEM_AMOUNT_100 || amount == GemStoreHUD.GEM_AMOUNT_250 || amount == GemStoreHUD.GEM_AMOUNT_750)
		{
			// launch the gas purchase UI flow.
			// We will be notified of completion via mPurchaseFinishedListener
			setWaitScreen(true);
			Log.d(TAG, "Launching purchase flow for gems.");
			
			RandomString randomString = new RandomString(36);
			String payload = randomString.nextString();
			payloadQueue.add(payload);
			
			mHelper.launchPurchaseFlow(context, SKU_GEMS + amount, RC_REQUEST, mPurchaseFinishedListener, payload);
		}
		
	}
	
	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p)
	{
		String payload = p.getDeveloperPayload();
		if(payloadQueue.contains(payload))
		{
			payloadQueue.remove(payload);
			return true;
		}
		return false;
	}
	
	void complain(String message)
	{
		Log.e(TAG, "**** BILLING Error: " + message);
		alert("Error: " + message);
	}
	
	void alert(String message)
	{
		AlertDialog.Builder bld = new AlertDialog.Builder(context);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}
	
	void setWaitScreen(boolean b)
	{
		hud.setLoading(b);
	}
	
	private void giveGems(int amount)
	{
		hud.giveGems(amount);
	}
	
	@Override
	public void eventResultReceived(EventResult event)
	{
		if(mHelper == null)
			return;
		
		// Pass on the activity result to the helper for handling
		if(mHelper.handleActivityResult(event.getRequestCode(), event.getResultCode(), (Intent) event.getDataObject()))
		{
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}
	
	boolean isDead = false;
	
	@Override
	public boolean isDead()
	{
		return isDead;
	}
	
	@Override
	public void dispose()
	{
		isDead = true;
		if(mHelper != null)
		{
			mHelper.dispose();
			mHelper = null;
		}
	}
	
	private class RandomString
	{
		private final Random random = new Random();
		
		private final char[] buf;
		
		public RandomString(int length)
		{
			if(length < 1)
				throw new IllegalArgumentException("length < 1: " + length);
			buf = new char[length];
		}
		
		public String nextString()
		{
			for(int idx = 0; idx < buf.length; ++idx)
				buf[idx] = symbols[random.nextInt(symbols.length)];
			return new String(buf);
		}
	}
}
