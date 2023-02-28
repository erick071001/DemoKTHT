package com.ebenezer.gana.shoppyv2.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.databinding.FragmentDashboardBinding
import com.ebenezer.gana.shoppyv2.firestore.FirestoreClass
import com.ebenezer.gana.shoppyv2.models.Products
import com.ebenezer.gana.shoppyv2.ui.activities.CartListActivity
import com.ebenezer.gana.shoppyv2.ui.adapters.DashboardListAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Type

class DashboardFragment : BaseFragment() {

    //private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding
    private var listproduct : ArrayList<Products> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = OkHttpClient()
        val moshi = Moshi.Builder().build()
        val usersType: Type = Types.newParameterizedType(
            MutableList::class.java,
            Products::class.java
        )
        val jsonAdapter: JsonAdapter<kotlin.collections.List<Products>> =
            moshi.adapter<kotlin.collections.List<Products>>(usersType)


        // Tạo request lên server.
        val request: Request = Request.Builder()
            .url("http://10.0.2.2:8000/product/")
            .build()

        Log.e("Request " , request.toString() )
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("Error Network Error", e.toString())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {

                // Lấy thông tin JSON trả về. Bạn có thể log lại biến json này để xem nó như thế nào.
                val json: String = response.body()!!.string()
                val users: kotlin.collections.List<Products>? = jsonAdapter.fromJson(json)
                Log.e("asssssssssss1", json.toString() )
                listproduct = ArrayList<Products>(users)
            }
        })
        listproduct.add(Products("1","Lonh2k5","san pham 1","12","13","10","12","123","12"))
        listproduct.add(Products("1","Lonh2k5","san pham 2","8","13","10","12","12","12"))
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemList()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding?.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_cart -> {

                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }


        return super.onOptionsItemSelected(item)

    }


    fun successDashboardItemsList(dashboardItemList: ArrayList<Products>) {
        hideProgressDialog()
        /* for (item in dashboardItemList){
             Log.i("Item Title", item.title)
         }*/

        binding?.let {
            if (dashboardItemList.size > 0) {
                binding!!.rvDashboardItems.visibility = View.VISIBLE
                binding!!.tvNoDashboardItemsFound.visibility = View.GONE

                // spanCount is set to 2 after every 5th item
                val layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                layoutManager.spanSizeLookup = object:GridLayoutManager.SpanSizeLookup(){
                    override fun getSpanSize(position: Int): Int {
                        return if((position + 1) % 5 == 0) 2 else 1
                    }
                }
                binding!!.rvDashboardItems.layoutManager = layoutManager

                binding!!.rvDashboardItems.setHasFixedSize(true)
                val allProductsAdapter = DashboardListAdapter(requireActivity(), dashboardItemList)
                binding!!.rvDashboardItems.adapter = allProductsAdapter

            } else {
                binding!!.rvDashboardItems.visibility = View.GONE

                binding!!.tvNoDashboardItemsFound.visibility = View.VISIBLE
            }
        }




    }

    private fun getDashboardItemList() {
        //show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(listproduct,this@DashboardFragment)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}