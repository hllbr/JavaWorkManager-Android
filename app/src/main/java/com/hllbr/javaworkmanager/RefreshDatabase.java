package com.hllbr.javaworkmanager;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RefreshDatabase extends Worker {
    //burad önemli olan worker sınıfını extends etmek

    Context myContext;


    public RefreshDatabase(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.myContext = context;//Bu yapı artık alt metodlar yada farklı yapılar içerisinde kullanılır durumda
    }

    @NonNull
    @Override
    public Result doWork() {
        //WorkManagerin ne yapacağını doWork altına yazıyoruz
        //bu yapı bize result adında bir obje bir sınıf döndürmemizi istiyor(Sonuç)
        Data data = getInputData();
        int myNumber = data.getInt("intkey",0);//MainActivityden gönderilen veriyi bu şekilde alabiliyorum

        refreshDatabase(myNumber);
        return Result.success();
    }
    private void refreshDatabase(int myNumber){//amacım veritabanını güncellemek

        /**Bu alanda veritabanını güncellemek için bir SharedPreferences kullanmak istiyorum
        *SharedPreferences sharedPreferences = this.getSharedPreferences demeke gerekiyor fakat bu gerçekleşmiyor sebebi ise şuan activity içerisinde değilimö
        *Bizim bu aladna this anahtar kelimesi yerine context'i kullanmamaız gerekiyor
         *Contexti direk olarak alamıyorum
         * getAppContext üzerinden alabiliriz uygulamanın kendi contexini kullanmak çalışır ama estetik bir yöntem değil
         * Ben global değişkenler arasına bri context tanımlar ve bunu kullanırsam daha estetik bir kullanım olur
        */

        SharedPreferences sharedPreferences =myContext.getSharedPreferences("com.hllbr.javaworkmanager",Context.MODE_PRIVATE);//Bu yapı benden bir veritabanı ismi istiyor.Paket isimi kullanmayı tercih ediyorum böyle bir durumda bu zorunlu bir durum değil
        //Mode_Private sadece bu yapıyı uygulama içersinde kullanmak istedimi belirtmek için eklediğim bir ifade
        //Bu yapı içerisine bir sayı kaydedip bunun her seferinde bir arttırılmasını istiyeceğim
        int mySavedNumber = sharedPreferences.getInt("myNumber",0);
        mySavedNumber = mySavedNumber+myNumber ;
        System.out.println("MySavedNumber : "+mySavedNumber);
        sharedPreferences.edit().putInt("myNumber",mySavedNumber).apply();
        //Bu yapı istediğim kadar çalıştırılabilir durumda





    }





}
