package com.example.ta.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.ta.Fragment.QtyFragment
import com.example.ta.Model.MCart
import com.example.ta.Model.MRatingBarang
import com.example.ta.Model.Url_Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_algo.view.*
import java.text.NumberFormat
import java.util.*


@Suppress("DEPRECATION")
public class RatingAdapter(var context:Context, var list:ArrayList<MRatingBarang>, var monlistener: OnNoteListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        var v: View = LayoutInflater.from(context).inflate(com.example.ta.R.layout.product_algo, p0, false)
        return ItemHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
        (p0 as ItemHolder).bind(
            list[p1].id,
            list[p1].judul_produk,
            list[p1].harga_normal,
            list[p1].deksripsi,
            list[p1].foto,
            list[p1].foto_type,
            list[p1].rating,
            p1,
            this.monlistener
        )

//        (context as ItemsAct).coordinator
//        (context as ItemAllAct).coordinator

//        (p0 as ItemHolder).bind(MItems(list[p1].id,list[p1].judul_produk,list[p1].harga_normal,list[p1].deksripsi,list[p1].foto,list[p1].foto_type),clickListener)

    }

    class ItemHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {
        lateinit var monlistener: OnNoteListener

        fun bind(id: Int, j: String, h: Double, dk: String, f: String, ft: String, r:Double, ps:Int, monlistener: OnNoteListener)
        {

            this.monlistener = monlistener
            var locale = Locale("in", "ID")
            var formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(locale)

            itemView.product_name.text = j
            itemView.rt_barangRek.rating = r.toFloat()
            itemView.urutan.text = (ps+1).toString()

//            itemView.item_deskripsi.text = dk
            itemView.product_price.text = formatRupiah.format(h)
            Picasso.with(itemView.context).load(Url_Volley.url_website+"/ecommerce/assets/images/produk/" + f + ft)
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

//    fun setFilter(filterList: ArrayList<MItemDetail>) {
//        var arrayList:ArrayList<MItemDetail> = ArrayList()
//        arrayList.addAll(filterList)
//        notifyDataSetChanged()
//    }

}
