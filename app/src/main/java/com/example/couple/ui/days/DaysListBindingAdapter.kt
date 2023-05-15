package com.example.couple.ui.days

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.couple.R
import com.example.couple.data.data.Days
import com.example.couple.databinding.DaysMemoryCellBinding
import com.example.couple.util.DateUtil
import java.time.LocalDate
import kotlin.math.abs

class DaysListBindingAdapter(val daysList: List<Days> = emptyList(), val daysViewModel: DaysViewModel) : RecyclerView.Adapter<DaysListBindingAdapter.DaysListViewHolder>(){
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysListViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = DaysMemoryCellBinding.inflate(inflater, parent, false)
        return DaysListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DaysListViewHolder, position: Int) {
        holder.bind(context, daysList[position], daysViewModel, position)
    }

    override fun getItemCount(): Int {
        return daysList.size
    }

    class DaysListViewHolder(binding: DaysMemoryCellBinding) :
            RecyclerView.ViewHolder(binding.root){
                val icon: ImageView = binding.daysMemoryCellSmallImg
                val date: TextView = binding.daysMemoryCellStartDateText
                val description: TextView = binding.daysMemoryCellDescription
                val count: TextView = binding.daysMemoryCellHowManyDays
                val card: View = binding.daysMemoryCellCard
                fun bind(context: Context, days: Days, daysViewModel: DaysViewModel, position: Int) {
                    date.text = days.date
                    icon.visibility = View.VISIBLE
                    description.text =
                        if(differentDays(days) < 0)
                            context.resources.getString(R.string.days_memory_cell_description_passed, days.type)
                        else
                            context.resources.getString(R.string.days_memory_cell_description_coming, days.type)
                    count.text =
                        context.resources.getString(R.string.days_memory_cell_days, abs(differentDays(days)).toString())
                    card.setOnClickListener {
                        Navigation.findNavController(it).navigate(R.id.action_navigation_days_to_navigation_days_edit)
                        daysViewModel.setSelectedDay(days)
                        daysViewModel.setSelectedPosition(position)
                        daysViewModel.isNavigateFromCard.postValue(true)
                    }
                }
            }
}

private fun differentDays(days: Days): Int {
    return DateUtil.differenceInDates(
        LocalDate.now().toString(),
        days.date
    )
}