package com.example.roomdatabasestudent

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.roomdatabasestudent.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var studentDatabase: StudentDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        studentDatabase = StudentDatabase.getData(this)

        binding.SaveBtn.setOnClickListener {
            saveData()
        }
        binding.SearchBtn.setOnClickListener {
            searchData()
        }
        binding.DeleteBtn.setOnClickListener {
            GlobalScope.launch {
                studentDatabase.studentDao().deleteAll()
            }
        }
    }

    private fun searchData() {
        val roll_No = binding.SearchEt.text.toString()
        if(roll_No.isNotEmpty()){
            lateinit var student: Student
            GlobalScope.launch {
                student = studentDatabase.studentDao().findByRollNo(roll_No.toInt())
                if(studentDatabase.studentDao().isEmpty())
                    Handler(Looper.getMainLooper()).post{
                        Toast.makeText(this@MainActivity, "Database have no data", Toast.LENGTH_SHORT).show()

                    }else{
                    displayData(student)

                }
            }
        }
    }

    private suspend fun displayData(student: Student) {
        withContext(Dispatchers.Main){
          binding.FirstName.setText(student.firstName.toString())
          binding.LastName.setText(student.lastName.toString())
          binding.RollNumber.setText(student.rollNumber.toString())
        }

    }

    private fun saveData() {
        val firstName = binding.FirstName.text.toString()
        val lastName = binding.LastName.text.toString()
        val rollNo = binding.RollNumber.text.toString()
        if(firstName.isNotEmpty() && lastName.isNotEmpty() && rollNo.isNotEmpty()){
            val student = Student(0,firstName,lastName,rollNo.toInt())
            GlobalScope.launch {
                studentDatabase.studentDao().insert(student)
            }
            binding.FirstName.text.clear()
            binding.LastName.text.clear()
            binding.RollNumber.text.clear()
            Toast.makeText(this@MainActivity, "Data Save", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this@MainActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }

    }

}