package ru.trueengineering.tefeaturetogglessdk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.trueengineering.tefeaturetoggles.FeatureTogglesSdk
import ru.trueengineering.tefeaturetogglessdk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModelsFactory {
        MainViewModel()
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        FeatureTogglesSdk.Initializer()
            .withInMemoryStorage()
            .headerKey("FFHASH")
            .baseUrl("http://localhost:8080")
            .initialize()

        lifecycleScope.launch {
            viewModel.error.collect {
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.button.setOnClickListener {
            viewModel.call()
        }
        binding.checkFlags.setOnClickListener {
            Toast.makeText(
                this,
                FeatureTogglesSdk.getFlags().toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}