package com.example.newmeas.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newmeas.Adapters.CustomAdapter
import com.example.newmeas.App
import com.example.newmeas.Data.Measures
import com.example.newmeas.Data.MeasuresVM
import com.example.newmeas.R
import com.example.newmeas.REALMS.RealmFactory
import com.example.newmeas.Utils.EventRealmCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MeasuresVM

    private lateinit var objAdapter: CustomAdapter

    var clickedName: Measures? = null

    private var menuItem: MenuItem? = null

    @Inject
    lateinit var realmFactory: RealmFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*
        Toolbar
         */
       /* setSupportActionBar(mainToolbar)
        val actionBar = supportActionBar
        actionBar!!.title = "Счетчики"
        actionBar.subtitle = "Управляй..."
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        //actionBar.setLogo(R.mipmap.ic_launcher)
        actionBar.setDisplayUseLogoEnabled(true)*/

        App.appComponent.injectMainActivity(this)
        realmFactory.setRealmConfiguration()

        registerForContextMenu(recyclerView)

        initVM()


        val fab = findViewById<FloatingActionButton>(R.id.add_fab)
        fab.setOnClickListener {

           //viewModel.insert("test" + Random.nextInt(0, 100),RealmList(1.33f, 0.22f, 1.32f))

            val intent = Intent(this, AddCounterActivity::class.java)
            startActivity(intent)
            finish()
            Realm.getDefaultInstance().close()
        }
    }

    /*
    обработка нажатий на элементе списка
     */
    private fun clickedRecyclerItem(mess: Measures) {
        openContextMenu(recyclerView)
        clickedName = mess
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(MeasuresVM::class.java)

        viewModel.data.observe(this, Observer { mutableList ->

            objAdapter = CustomAdapter(mutableList) { mess: Measures -> clickedRecyclerItem(mess) }
            recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            recyclerView.adapter = objAdapter
        })
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        menu!!.setHeaderTitle("Действия:")

        menu.run {
            add(0, v!!.id, 0, "Открыть")
            add(0, v.id, 1, "Удалить")

        }


    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {

        val itemMenu = item?.title

        if (clickedName != null) {
            when (itemMenu) {
                "Удалить" -> {
                    viewModel.delete(clickedName!!.name, object : EventRealmCallback {
                        override fun onComplete() {
                            Toast.makeText(applicationContext, "Deleted, got and updated!", Toast.LENGTH_SHORT).show()
                            objAdapter.updateList(viewModel.getAll()!!)
                            recyclerView.adapter = objAdapter
                        }
                    })
                }
                "Открыть" -> {
                    val intent = Intent(this, AddCounterActivity::class.java)
                    intent.putExtra("name", clickedName!!.name)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu1,menu)

       /* КОД ЗАМЕНЫ */
       menuItem = menu!!.findItem(R.id.saveInToolbar)
       /* menuItem.icon = ContextCompat.getDrawable(this, R.drawable.abc_ic_star_black_48dp)*/


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.saveInToolbar -> {
                Toast.makeText(applicationContext,"test1", Toast.LENGTH_LONG).show()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


}

