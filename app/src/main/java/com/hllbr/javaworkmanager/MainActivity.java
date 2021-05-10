package com.hllbr.javaworkmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Bundle;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Data data = new Data.Builder().putInt("intkey",1).build();  //Bu şekilde diğer tarafa ne göndermek istiyorsak göndrebiliriz.
        /**
         * Biz bazı constrateler belirtebiliyoruz
         * 1 yapı çalışırken şarj var mı yok mu ona bakılsın sadece şarj ediliyorsa çalışsın
         * 2 network tipine bakılsın sadece internete bağlıysa çalıştırılsın
         * ....
         */
        Constraints constraints = new Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresCharging(false)
                .build();







        // .setRequiredNetworkType() = bağlantı durumunu seçebiliyorum
        // .setRequiresCharging(false)//şarja bağlı olsun(false = olmasın demek)




        //Şuana kadar veriyi yaptık constraits yaptık şimdi iş isteğini yani workRequest oluşturmam gerekiyor
        //birden fazla workRequest var bir tane genel olan bulunuyor bu base class herşey için kullanabiliyoruz
        //Spesifik olanlar var sadece bir defa yapıacak yada sadece belirttiğimiz aralıklarda yapılacak.Ben Projemde Basic olanı kullanmaya çalışacağım
       /*
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(RefreshDatabase.class)
        .setConstraints(constraints)
                .setInputData(data)
                .build();

        sadece birkez çalıştırmak istiyorum burda benden bir adet worker class isteniyor.Burada daha önce oluşturduğum RefreshDatabase kullanıyorumbundan sonra tek tek buil etmem gerekiyor yani inşa etmem
        5 dakika sonra
        .addTag("myTag")//birden fazla workrequestimiz varsa  2 ayrıştırma yöntemimiz var bunlardan biri taglar(atadığımız etiketlere göre) 2. olarak kendisi bir id atıyor ona göre ayrıştırabiliriz.


        üsteki yapıyı işleme almak için WorkManager çağrıyorum

        WorkManager.getInstance(this).enqueue(workRequest);//sıraya alarak çalıştırıyoruz
        Şuana kadar bir defa çalışması için yani onCreate her çalıştırıldığında bir defa artacak şekilde ayarladım
        */

        WorkRequest workRequest = new PeriodicWorkRequest.Builder(RefreshDatabase.class,15,TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(data)
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);
        //WorkRequest içerisinde süreyi min 15 dakika oalrak belirleyebiliyorum androidin kendi koyduğu sınırlardan biri bu
        //Uygulamayı kapattım 15 dakika sonra bir artış olması gerekiyor.


        /*Zincirleme ve Gözlemleme ???
        Yapılan işin yapılan wrok'ün ne durumda olduğunu görmek istiyorsak runnig-fail-success gibi  durumlarda bunlar değiştiğinde bize bildirimgelmesini ve bu durumlara göre işlem yapmak istiyorsak ....


         */

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                //burada bana workInfo yani iş bilgisi olarka bir sınıf döndürülüyor.
                //Değiştirilince ne olack adlı bu sınıfın içerisinde bu bilgi bana veriliyor
                //bir koşul yazarak şuanki işin durumunu alarak işlemler yapabilirim
                if(workInfo.getState() == WorkInfo.State.RUNNING){
                    //şuan çalıştırılıyorsa
                    System.out.println("Running");
                }else if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    //Olay Başarı ile sonuçlandı
                    System.out.println("Succeded");
                }else if(workInfo.getState() == WorkInfo.State.ENQUEUED){
                    System.out.println("Enqueued");//sırada
                }else if(workInfo.getState() == WorkInfo.State.FAILED){
                    //başarısızlıkla sonuçlanırsa
                    System.out.println("Failed");
                }else if(workInfo.getState() == WorkInfo.State.BLOCKED){
                    //bloklanırsa
                    System.out.println("Blocked");
                }
                //Bu koşulların altında Toast messagelar yada daha farklı şekiller kullanılabilir.Bu alanı kullanıcıyı bilgilendirmek için kullanabilir yada sadce yapının testlerini gerçekleştirmek içinde kullanılabilir yapılan işlemlerin önemi ve kullanıcılara bu işlemler hakkında ne kadar bilgi vermeniz gerektiği böyle durumlarda devreye giriyor.

            }
        });//daha önce söyledğim tag yada idye göre ayırabiliyoruz dedim kısım işte bu noktada işime yarıyor

        //.observe benden yaşam döngüsünün sahibi kim onu istiyor this diyebilirim.2. olarak ikinci olarak bir gözlemci istiyor bir listener oluşturrarak bu ihtiyacı giderebilirim






        //İptal etme işlemleri
        //WorkManager.getInstance(this).cancelAllWork()//tüm Workleri iptal et (birden fazla work oluşturabiliriz.)
        //WorkManager.getInstance(this).cancelAllWorkByTag()//Tage göre iptal et
        //WorkManager.getInstance(this).cancelWorkById()//idye göre iptal et




        //Chaning = Zincirleme & Bağlama

       // Periodik olan iş isteklerini birbirlerine bağlayamıyorum.(BAĞLAYAMIYORUM) this info criticsssssss


        //Birbirine sadece bir defa yapılanlar bağlanabiliyor



        //Arka arkaya bir işlem yaptırmak istediğimizde bunu kullanabilirz(Arkaplanda)
       // Bunu oluştururken WorkRequest diyerek yapamayız direkt olarak alt satırdaki hali ile yapmamız gerekiyor
              /*  OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(RefreshDatabase.class)
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build();


                //Bundan birkaç tane olduğu düşünülürse

        WorkManager.getInstance(this).beginWith(oneTimeWorkRequest)
        .then(oneTimeWorkRequest)
                .then(oneTimeWorkRequest)
                .enqueue();


                    başlayacağımız bir listede olabilir yada tekbir requestte olabilir
    */










    }

}