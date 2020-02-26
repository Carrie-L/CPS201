package com.adsale.chinaplas.ui.events


/**
 * 技术交流会第一天
 */
class SeminarOneFragment : BaseSeminarFragment() {
    override fun init() {
        CURRENT_DATE_INDEX = 1
    }


//    private lateinit var recyclerView: RecyclerView
//    private lateinit var seminarViewModel: SeminarViewModel
//    private lateinit var adapter: SeminarAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.layout_recycler_view, container, false)
//        recyclerView = view.findViewById(R.id.recycler_view)
//        return view
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        initView()
//        initData()
//    }
//
//    private fun initView() {
//        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        adapter = SeminarAdapter(listOf(), itemClickListener)
//        recyclerView.adapter = adapter
//    }
//
//    private fun initData() {
//        seminarViewModel = ViewModelProviders.of(this, SeminarViewModelFactory(
//            SeminarRepository.getInstance(CpsDatabase.getInstance(requireContext()).seminarDao())
//        )).get(SeminarViewModel::class.java)
//
//        LogUtil.i("initData vvvvv")
//
//        seminarViewModel.getSeminarTimeList(true)
//
//        seminarViewModel.seminarList.observe(this, Observer {
//            adapter.setList(it)
//        })
//    }
//
//    private val itemClickListener = OnItemClickListener { entity, pos ->
//
//    }
//
//    companion object {
//        @Volatile
//        private var instance: SeminarOneFragment? = null
//
//        fun getInstance() =
//            instance ?: synchronized(this) {
//                instance ?: SeminarOneFragment().also { instance = it }
//            }
//    }


}
