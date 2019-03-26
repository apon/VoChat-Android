package me.apon.vochat

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import me.apon.vochat.db.AppRoomDatabase
import me.apon.vochat.db.TestData
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/13.
 */
@RunWith(AndroidJUnit4::class)
class TestDataDaoTest {
    var database:AppRoomDatabase? = null

    @Before
    fun initDb(){
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
            AppRoomDatabase::class.java).build()
    }
    @Test
    fun insert(){
        database?.testDataDao()?.insert(TestData(0,"yaopeng",18))
        database?.testDataDao()?.insert(TestData(0,"apon",18))
        val list = database?.testDataDao()?.getAll()
//        Log.d("TEST",list.toString())
        System.out.println(list.toString())
        Assert.assertEquals(2,list?.size)
    }

    @After
    fun closeDb(){
        database?.close()
    }
}