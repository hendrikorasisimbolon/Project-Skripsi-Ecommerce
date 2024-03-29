package com.example.ta.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ta.Fragment.QtyFragment
import com.example.ta.Model.MCart
import com.example.ta.Model.MItemDetail
import com.haozhang.lib.SlantedTextView
import kotlinx.android.synthetic.main.product_row_item.view.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchAdapter (var context: Context, var list:ArrayList<MItemDetail>, var monlistener:OnNoteListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        var v: View = LayoutInflater.from(context).inflate(com.example.ta.R.layout.product_row_item, p0, false)
        return ItemHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
        (p0 as ItemHolder).bind(
            list[p1].id,
            list[p1].judul_produk,
            list[p1].harga_diskon,
            list[p1].deskripsi,
            list[p1].foto,
            list[p1].foto_type,
            list[p1].diskon,
            list[p1].harga_normal,
            this.monlistener
        )

//        (context as ItemsAct).coordinator
//        (context as ItemAllAct).coordinator

//        (p0 as ItemHolder).bind(MItems(list[p1].id,list[p1].judul_produk,list[p1].harga_normal,list[p1].deksripsi,list[p1].foto,list[p1].foto_type),clickListener)

    }

    public class ItemHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        lateinit var monlistener: OnNoteListener

        fun bind(id: Int, j: String, h: Double, dk: String, f: String, ft: String,ds:Double, hn:Double, monlistener: OnNoteListener)
        {
            this.monlistener = monlistener
            var locale = Locale("in", "ID")
            var formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(locale)
            val nf = NumberFormat.getNumberInstance()
            nf.maximumFractionDigits = 0

            itemView.product_name.text = j
//            itemView.item_deskripsi.text = dk
//            itemView.product_price.text = formatRupiah.format(h)

            if(ds>0){
                itemView.product_price_diskon.text = formatRupiah.format(hn)
                itemView.product_price_diskon.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                itemView.product_price.text = formatRupiah.format(h)
                itemView.stvDiscount.setText("Disc."+nf.format(ds)+" %")
                    .setTextColor(Color.WHITE)
                    .setSlantedLength(70)
                    .setMode(SlantedTextView.MODE_LEFT)
            }
            else
            {
                itemView.product_price.text = formatRupiah.format(hn)
                itemView.product_price_diskon.visibility = View.INVISIBLE
                itemView.stvDiscount.visibility = View.INVISIBLE
            }


            Glide.with(itemView.context).load("http://192.168.43.180/ecommerce/assets/images/produk/" + f + ft)
                .into(itemView.product_image)

            itemView.addToCart.setOnClickListener{
                MCart.itemId=id
                var obj = QtyFragment() // fragment
                var manager = (itemView.context as AppCompatActivity).fragmentManager //convert fragment ke activity dengan manager
                obj.show(manager, "Qty")


            }
            itemView.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            monlistener.onNoteClick(adapterPosition)
        }

    }

    interface OnNoteListener {
        fun onNoteClick(position: Int)
    }


    fun setFilter(a:ArrayList<MItemDetail>)
    {
        list = ArrayList()
        list.addAll(a)
        notifyDataSetChanged()
    }

    fun clear(){
        list.clear()
        list = ArrayList()
        notifyDataSetChanged()
    }

}