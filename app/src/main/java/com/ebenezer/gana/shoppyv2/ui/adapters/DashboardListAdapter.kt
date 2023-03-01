package com.ebenezer.gana.shoppyv2.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.databinding.ItemDashboardLayoutBinding
import com.ebenezer.gana.shoppyv2.models.Product
import com.ebenezer.gana.shoppyv2.ui.activities.ProductDetailsActivity
import com.ebenezer.gana.shoppyv2.utils.GlideLoader

class DashboardListAdapter(
    private val context: Context,
    private var allProducts: ArrayList<Product>
) : RecyclerView.Adapter<DashboardListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDashboardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var product: Product

        fun bind(product: Product) {
            this.product = product


            GlideLoader(context).loadProductPicture(
                product.imageURL,
                binding.ivDashboardItemImage
            )

            binding.tvDashboardItemTitle.text = product.product_name
            binding.tvDashboardItemPrice.text =
                "${product.price}$"
            binding.tvDashboardItemDescription.text =
                product.description

            itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra("Product", product)
                context.startActivity(intent)

            }

            animateView(itemView)


        }

        private fun animateView(viewToAnimate: View) {
            if (viewToAnimate.animation == null) {
                val animation = AnimationUtils.loadAnimation(
                    viewToAnimate.context, R.anim.scale_xy
                )
                viewToAnimate.animation = animation
            }
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(allProducts[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDashboardLayoutBinding.inflate(
            LayoutInflater.from(context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = allProducts.size


}



