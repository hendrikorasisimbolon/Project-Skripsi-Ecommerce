package com.example.ta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ta.Model.Url_Volley.Companion.url_website
import kotlinx.android.synthetic.main.activity_blog_detail.*
import kotlinx.android.synthetic.main.activity_blog_detail.view.*

class BlogDetailAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_detail)
        var judul = intent.getStringExtra("judul_blog").toString()
        var isi = intent.getStringExtra("isi_blog").toString()
        var pengarang = intent.getStringExtra("pengarang_blog").toString()
        var foto_blog = intent.getStringExtra("foto_blog").toString()
        var foto_type_blog = intent.getStringExtra("foto_type_blog").toString()

        txt_judul.text = judul
        txt_pengarang.text = "by :" + pengarang

        Glide.with(img_blog.context).load(url_website+"/ecommerce/assets/images/blog/"+foto_blog+foto_type_blog)
            .into(img_blog.img_blog)

        isi_Web.loadData(isi,"text/html","utf-8")
    }



}
