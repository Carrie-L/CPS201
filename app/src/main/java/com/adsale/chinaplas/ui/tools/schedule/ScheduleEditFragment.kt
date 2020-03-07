package com.adsale.chinaplas.ui.tools.schedule


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.data.dao.ScheduleInfo
import com.adsale.chinaplas.data.dao.ScheduleRepository
import com.adsale.chinaplas.databinding.FragmentScheduleEditBinding
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory

/**
 * [isAdd] true : 添加， false 编辑
 * [id]  add :  eventID / companyID ; update : row id
 * [type] 1: 展商 ； 2: 同期活动
 */
class ScheduleEditFragment : Fragment() {
    private lateinit var binding: FragmentScheduleEditBinding
    private lateinit var scheduleViewModel: ScheduleViewModel
    private var type = 1
    private var id = ""
    private var isAdd = true
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentScheduleEditBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var entity: ScheduleInfo? = null
        arguments?.let {
            isAdd = ScheduleEditFragmentArgs.fromBundle(it).isAdd
            entity = ScheduleEditFragmentArgs.fromBundle(it).info
        }
        initViewModel()
        if (isAdd) {
            mainViewModel.title.value = getString(R.string.addScheduleInfo)
        } else {
            mainViewModel.title.value = getString(R.string.edit_schedule)
        }

        scheduleViewModel.isAdd = isAdd
        scheduleViewModel.type = entity!!.type
        scheduleViewModel.id = entity!!.pageID

        if (!isAdd) {
            // 编辑日程
            scheduleViewModel.setDetailInfo(entity!!)
        } else {
            // 添加日程
            scheduleViewModel.also {
                it.title.value = entity!!.title
                it.location.value = entity!!.location
            }
        }

        if (entity!!.type != 1) {
            binding.imgExhibitor.visibility = View.GONE
        }


        binding.btnDel.setOnClickListener {
            scheduleViewModel.onDelete()
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            if (scheduleViewModel.title.value!!.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.not_title), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            scheduleViewModel.onSave()
            findNavController().popBackStack()
            Toast.makeText(requireContext(), getString(R.string.note_successful), Toast.LENGTH_SHORT).show()
        }

        binding.imgExhibitor.setOnClickListener {
            if (isAdd && entity!!.type == 1) {
                findNavController().popBackStack()
            } else
                findNavController().navigate(ScheduleEditFragmentDirections.actionToExhibitorDetailFragment(entity!!.pageID))
        }

    }

    private fun initViewModel() {
        mainViewModel = ViewModelProviders.of(
            requireActivity(), MainViewModelFactory(
                requireActivity().application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
            )
        ).get(MainViewModel::class.java)

        scheduleViewModel = ViewModelProviders.of(this,
            ScheduleViewModelFactory(ScheduleRepository.getInstance(CpsDatabase.getInstance(requireContext()).scheduleDao())))
            .get(ScheduleViewModel::class.java)
        binding.scheduleModel = scheduleViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }


}
