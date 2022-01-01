package org.northwinds.amsatstatus;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

public class HomeFragmentFactory extends FragmentFactory {
    @Override
    @NonNull
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
        Class[] cArg = new Class[2];
        cArg[0] = Clock.class;
        cArg[1] = Clock.class;
        try {
            Class<? extends Fragment> cls = loadFragmentClass(classLoader, className);
            return cls.getConstructor(cArg).newInstance(new Clock(), new Clock());
        } catch (Exception e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": bad stuff happened", e);
        }
    }
}