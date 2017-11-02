package dakma.waplak.lk.dakmapro.home;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dakma.waplak.lk.dakmapro.R;

import static dakma.waplak.lk.dakmapro.home.Utils.setupItem;

/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {

    private final Utils.LibraryObject[] LIBRARIES = new Utils.LibraryObject[]{
            new Utils.LibraryObject(
                    R.mipmap.zero,
                    "\n"+
                    "Dekma Institute\n"+
                            "Matara"
            ),
            new Utils.LibraryObject(
            R.mipmap.three,
                    "Rashan Chanuka Abeydeera\n" +
                            "2016 Advance Level Maths\n" +
                            "Hambantota District 1st\n" +
                            "H/Rajapaksha Central College"
            ),
            new Utils.LibraryObject(
                    R.mipmap.two,
                    "Shehan Munasinghe\n" +
                            "2016 Advance Level Maths\n" +
                            "Matara District 1st\n" +
                            "Island 4th\n" +
                            "Rahula College"
            ),
            new Utils.LibraryObject(
                    R.mipmap.one,
                    "Chathura Jayasanka\n" +
                            "2016 Advance Level Maths\n" +
                            "Galle District 1st\n" +
                            "Island 2nd\n" +
                            "Richmond College"
            )
//            new Utils.LibraryObject("Matara"
//            ),
//            new Utils.LibraryObject("Galle"
//            ),
//            new Utils.LibraryObject("Hambantota"
//            )
    };

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    //private boolean mIsTwoWay;

    public HorizontalPagerAdapter(final Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        //mIsTwoWay = isTwoWay;
    }

    @Override
    public int getCount() {
        return  LIBRARIES.length;
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view;

            view = mLayoutInflater.inflate(R.layout.item, container, false);
            setupItem(view, LIBRARIES[position]);


        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
