/*
 * TV-Browser for Android
 * Copyright (C) 2013 René Mach (rene@tvbrowser.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to use, copy, modify or merge the Software,
 * furthermore to publish and distribute the Software free of charge without modifications and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.tvbrowser.tvbrowser;

import org.tvbrowser.content.TvBrowserContentProvider;
import org.tvbrowser.settings.SettingConstants;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    long programID = intent.getLongExtra(SettingConstants.REMINDER_PROGRAM_ID_EXTRA, -1);
    
    if(programID >= 0) {
      Cursor values = context.getContentResolver().query(ContentUris.withAppendedId(TvBrowserContentProvider.CONTENT_URI_DATA_WITH_CHANNEL, programID), SettingConstants.REMINDER_PROJECTION, null, null, TvBrowserContentProvider.DATA_KEY_STARTTIME);
      
      if(values.getCount() > 0 && values.moveToNext()) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        
        String channelName = values.getString(values.getColumnIndex(TvBrowserContentProvider.CHANNEL_KEY_NAME));
        String title = values.getString(values.getColumnIndex(TvBrowserContentProvider.DATA_KEY_TITLE));
        String episode = values.getString(values.getColumnIndex(TvBrowserContentProvider.DATA_KEY_EPISODE_TITLE));
        
        long startTime = values.getLong(values.getColumnIndex(TvBrowserContentProvider.DATA_KEY_STARTTIME));
        long endTime = values.getLong(values.getColumnIndex(TvBrowserContentProvider.DATA_KEY_ENDTIME));
        
        boolean hasLogo = !values.isNull(values.getColumnIndex(TvBrowserContentProvider.CHANNEL_KEY_LOGO));
                
        if(hasLogo) {
          byte[] logoData = values.getBlob(values.getColumnIndex(TvBrowserContentProvider.CHANNEL_KEY_LOGO));
          
          if(logoData.length > 0) {
            Bitmap logo = BitmapFactory.decodeByteArray(logoData, 0, logoData.length);
            
            int width =  context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
            int height = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
            
            float scale = 1;
            
            if(logo.getWidth() > width-4) {
              scale = ((float)width-4)/logo.getWidth();
            }
            
            if(logo.getHeight() * scale > height-4) {
              scale = ((float)height-4)/logo.getHeight();
            }
            
            if(scale < 1) {
              logo = Bitmap.createScaledBitmap(logo, (int)(logo.getWidth() * scale), (int)(logo.getHeight() * scale), true);
            }
            
            Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(SettingConstants.LOGO_BACKGROUND_COLOR);
            canvas.drawBitmap(logo, width/2 - logo.getWidth()/2, height/2 - logo.getHeight()/2, null);
            
            builder.setLargeIcon(bitmap);
          }
        }
        
        builder.setSmallIcon(R.drawable.reminder);
        builder.setWhen(startTime);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setVibrate(new long[] {1000,1000,1000,1000,1000});
        builder.setAutoCancel(true);
        builder.setContentInfo(channelName);
        builder.setLights(Color.RED, 1, 0);
        
        builder.setContentTitle(title);
        
        if(episode != null) {
          builder.setContentText(episode);
        }
        
        Intent startInfo = new Intent(context, InfoActivity.class);
        startInfo.putExtra(SettingConstants.REMINDER_PROGRAM_ID_EXTRA, programID);
        startInfo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        builder.setContentIntent(PendingIntent.getActivity(context, 0, startInfo, PendingIntent.FLAG_UPDATE_CURRENT));
        
        Notification notification = builder.build();
        
        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(title,(int)(startTime / 60000), notification);
      }
    }
  }

}