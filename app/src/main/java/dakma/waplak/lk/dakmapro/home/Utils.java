package dakma.waplak.lk.dakmapro.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dakma.waplak.lk.dakmapro.R;


/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class Utils {

    public static void setupItem(final View view, final LibraryObject libraryObject) {
          //final ImageView img=(ImageView)view.findViewById(R.id.imageView);

        final TextView txt = (TextView) view.findViewById(R.id.txt_item);
        txt.setText(libraryObject.getTitle());
//
        final ImageView img = (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(libraryObject.getRes());
    }

        public static class LibraryObject {

        private String mTitle;
        private int mRes;

        public LibraryObject(final int res, final String title) {
            mRes = res;
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(final String title) {
            mTitle = title;
        }

        public int getRes() {
            return mRes;
        }

        public void setRes(final int res) {
            mRes = res;
        }
    }
}
