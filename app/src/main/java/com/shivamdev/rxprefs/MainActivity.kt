package com.shivamdev.rxprefs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.bClear
import kotlinx.android.synthetic.main.activity_main.bSubmit
import kotlinx.android.synthetic.main.activity_main.etName
import kotlinx.android.synthetic.main.activity_main.tvName

class MainActivity : AppCompatActivity() {

    private lateinit var repository: NameRepository
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repository = SharedPreferencesNameRepository.create(this)
        handleData()
    }

    private fun handleData() {
        bSubmit.setOnClickListener {
            val name = etName.toText()
            disposables.add(repository.saveName(name).subscribe())
        }

        disposables.add(repository.name().subscribe({
            tvName.text = it
        }, {
            Log.e("Error : ", "", it)
        }))

        bClear.setOnClickListener {
            disposables.add(repository.clear().subscribe())
        }
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}

private fun EditText.toText() = text.toString()
private fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
