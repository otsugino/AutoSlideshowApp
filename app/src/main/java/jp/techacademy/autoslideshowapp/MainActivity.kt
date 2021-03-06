package jp.techacademy.autoslideshowapp


import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Build
import android.content.pm.PackageManager
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.provider.Contacts
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    // パーミッションが許可されていれば１、そうでなければ0
    private var permission_allow:Int = 0


    private var photo_no:Int = 0
    private var photo_count:Int = 0
    val ary_photo_id: Array<Long?> = arrayOfNulls(100)

    private var mTimer: Timer? = null


    private var mHandler = Handler()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("ANDROID","パーミッションチェック前"+permission_allow)
        permissionCheck()





        playstop_button.setOnClickListener {
            Log.d("ANDROID","push_playstop")
            Log.d("ANDROID",photo_no.toString())

            if(permission_allow == 1) {
                if(playstop_button.text == "再生"){
                    playstop_button.text = "停止"
                    forward_button.setEnabled(false)
                    back_button.setEnabled(false)

                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {

                            mHandler.post {
                                countup_photo_no()
                            }
                        }
                    }, 2000, 2000)
                }else{
                    playstop_button.text = "再生"
                    forward_button.setEnabled(true)
                    back_button.setEnabled(true)

                    mTimer!!.cancel()
                }
            }else{
                Toast.makeText(this,"権限が許可されていません。",Toast.LENGTH_SHORT).show()
            }








        }

        forward_button.setOnClickListener {
            Log.d("ANDROID","push_forward")
            if(permission_allow == 1) {
                countup_photo_no()

            }else{
                Toast.makeText(this,"権限が許可されていません。",Toast.LENGTH_SHORT).show()
            }
        }

        back_button.setOnClickListener {
            Log.d("ANDROID","push_back")
            if(permission_allow == 1) {
                countdown_photo_no()
            }else{
                Toast.makeText(this,"権限が許可されていません。",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun permissionCheck(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0以降の場合
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                permission_allow = 1
                Log.d("ANDROID","許可されている。パーミッションチェック後"+permission_allow)
                getContentsInfo1()

            } else {
                // 許可されていないので許可ダイアログを表示する
                Log.d("ANDROID", "許可されていない"+permission_allow)
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            permission_allow = 1
            Log.d("ANDROID","Android5以下。パーミッションチェック後"+permission_allow)
            getContentsInfo1()
        }



    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission_allow = 1
                    Log.d("ANDROID","許可された。パーミッションチェック後"+permission_allow)
                    getContentsInfo1()

                }else{
                    Log.d("ANDROID","許可されなかった。パーミッションチェック後"+permission_allow)

                }
        }
    }

    private fun getContentsInfo1() {
        Log.d("ANDROID","実行：getContentInfo1")
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            ary_photo_id[0] = id

            imageView.setImageURI(imageUri)

            var i = 0
            while(cursor.moveToNext()){
                i ++
                Log.d("ANDROID",i.toString())

                ary_photo_id[i] = cursor.getLong(fieldIndex)
                Log.d("ANDROID",ary_photo_id[i].toString())

            }

            photo_count = i


            Log.d("ANDROID",ary_photo_id[0].toString())
            Log.d("ANDROID",photo_count.toString())
        }
        cursor.close()

    }

    private fun countup_photo_no() {

        photo_no ++

        if(photo_no > photo_count){
            photo_no = 0
        }

        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,ary_photo_id[photo_no]!!)

        imageView.setImageURI(imageUri)
        Log.d("ANDROID", ary_photo_id[photo_no]!!.toString())
    }

    private fun countdown_photo_no() {
        photo_no --

        if(photo_no < 0){
            photo_no = photo_count
        }

        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,ary_photo_id[photo_no]!!)

        imageView.setImageURI(imageUri)
        Log.d("ANDROID", ary_photo_id[photo_no]!!.toString())
    }
}