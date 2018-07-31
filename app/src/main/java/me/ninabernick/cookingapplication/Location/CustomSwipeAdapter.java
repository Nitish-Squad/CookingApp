package me.ninabernick.cookingapplication.Location;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.InputStream;

import me.ninabernick.cookingapplication.R;

public class CustomSwipeAdapter extends PagerAdapter {

    private String[] images;
    private Context ctx;
    private LayoutInflater layoutInflator;

    public CustomSwipeAdapter(Context ctx, String [] images) {
        this.ctx = ctx;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return (view == (RelativeLayout)o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflator.inflate(R.layout.storedetails_imageswipelayout, container, false);
        ImageView imageView = (ImageView) item_view.findViewById(R.id.image_view);
        new DownloadImageTask(imageView).execute(photourl(images[position]));
        container.addView(item_view);

        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }

    public String photourl(String photoreference) {
        StringBuilder photosb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400");
        photosb.append("&photoreference=" + photoreference);
        photosb.append("&key=AIzaSyD_gosGg3qBnX2WOj6-fglzL49kTMO-KuY");
        Log.i("checking photo url", "checking photo url: " + photosb.toString());
        return photosb.toString();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
