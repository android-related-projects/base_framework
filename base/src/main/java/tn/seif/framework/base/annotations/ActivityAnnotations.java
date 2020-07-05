package tn.seif.framework.base.annotations;

import android.view.View;

import androidx.fragment.app.FragmentActivity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public final class ActivityAnnotations {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ViewById {
        int value() default -1;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface LayoutId {
        int value() default -1;
    }

    public static final class Utils {
        public static int getLayoutId(Class<? extends FragmentActivity> instanceClass) throws Exceptions.NoLayoutException {
            LayoutId annotation;
            if (instanceClass.isAnnotationPresent(LayoutId.class) && (annotation = instanceClass.getAnnotation(LayoutId.class)) != null) {
                return annotation.value();
            }
            throw new Exceptions.NoLayoutException();
        }

        public static <A extends FragmentActivity> void setupViewsById(A activity) throws IllegalAccessException, Exceptions.ViewIdNotSetException, Exceptions.ViewNotFoundException {
            for (Field field : activity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                ViewById annotation;
                if (field.isAnnotationPresent(ViewById.class) && (annotation = field.getAnnotation(ViewById.class)) != null) {
                    int viewId = annotation.value();
                    if (viewId <= 0) {
                        throw new Exceptions.ViewIdNotSetException();
                    } else {
                        View view;
                        if ((view = activity.findViewById(viewId)) == null) {
                            throw new Exceptions.ViewNotFoundException(viewId);
                        } else {
                            field.set(activity, view);
                        }
                    }
                }
            }
        }
    }

    public static final class Exceptions {

        public static class NoLayoutException extends Exception {
            public NoLayoutException() {
                super("No Layout exception");
            }
        }

        public static class ViewIdNotSetException extends Exception {
            public ViewIdNotSetException() {
                super("View ID is not set");
            }
        }

        public static class ViewNotFoundException extends Exception {
            public ViewNotFoundException(int id) {
                super("View with ID: " + id + " not found");
            }
        }

    }
}
