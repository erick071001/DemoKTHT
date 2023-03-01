package com.ebenezer.gana.shoppyv2.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.databinding.ListItemCartBinding
import com.ebenezer.gana.shoppyv2.firestore.FirestoreClass
import com.ebenezer.gana.shoppyv2.models.Cart
import com.ebenezer.gana.shoppyv2.ui.activities.CartListActivity
import com.ebenezer.gana.shoppyv2.utils.Constants
import com.ebenezer.gana.shoppyv2.utils.GlideLoader

class CartListAdapter(
    private val context: Context,
    private var cartListItems: ArrayList<Cart>,
    private val updateCartItems: Boolean
) : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ListItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartListItems[position])

    }

    override fun getItemCount(): Int {

        return cartListItems.size
    }

    inner class ViewHolder(val binding: ListItemCartBinding) : View.OnClickListener,

        RecyclerView.ViewHolder(binding.root) {
        private lateinit var cart: Cart

        fun bind(cart: Cart) {
            this.cart = cart

            GlideLoader(context).loadProductPicture(
                cart.product.imageURL,
                binding.ivCartItemImage
            )


            binding.tvCartItemTitle.text = cart.product.product_name
            binding.tvCartItemPrice.text = "${cart.product.price}$"
            binding.tvCartQuantity.text = cart.product.totalQuantity


            binding.ibDeleteCartItem.setOnClickListener(this)
            binding.ibAddCartItem.setOnClickListener(this)
            binding.ibRemoveCartItem.setOnClickListener(this)

            if (cart.product.totalQuantity == "0") {
                binding.ibRemoveCartItem.visibility =
                    View.GONE
                binding.ibAddCartItem.visibility =
                    View.GONE


                if (updateCartItems) {
                    binding.ibDeleteCartItem
                        .visibility = View.VISIBLE
                } else {
                    binding.ibDeleteCartItem
                        .visibility = View.GONE
                }

                binding.tvCartQuantity.text =
                    context.resources.getString(R.string.lb_out_of_stock)

                binding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.colorSnackBarError
                    )
                )

            } else {

                if (updateCartItems) {
                    binding.ibRemoveCartItem.visibility =
                        View.VISIBLE
                    binding.ibAddCartItem.visibility =
                        View.VISIBLE
                    binding.ibDeleteCartItem
                        .visibility = View.VISIBLE

                } else {
                    binding.ibRemoveCartItem.visibility =
                        View.GONE
                    binding.ibAddCartItem.visibility =
                        View.GONE
                    binding.ibDeleteCartItem
                        .visibility = View.GONE

                }

                binding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.colorSecondaryText
                    )
                )

            }

        }

        override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {
                    R.id.ib_delete_cart_item -> {
                        FirestoreClass().removedItemFromCart(context, cart.product.productId)
                    }
                    R.id.ib_remove_cart_item -> {
                        if (cart.product.totalQuantity == "1") {
                            FirestoreClass()
                                .removedItemFromCart(context, cart.product.productId)
                        } else {
                            val cartQuantity: Int = cart.product.totalQuantity.toInt()

                            val itemHashMap = HashMap<String, Any>()

                            itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                            FirestoreClass()
                                .updateMyCart(context, itemHashMap)
                        }
                    }
                    R.id.ib_add_cart_item -> {
                        val cartQuantity: Int = cart.product.totalQuantity.toInt()

                        if (cartQuantity < cart.product.totalQuantity.toInt()) {
                            val itemHashMap = HashMap<String, Any>()

                            itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                            FirestoreClass().updateMyCart(context, itemHashMap)


                        } else {
                            if (context is CartListActivity) {
                                context.showErrorSnackBar(
                                    context.resources.getString(
                                        R.string.msg_for_available_stock,
                                        cart.product.productId
                                    ), true
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}