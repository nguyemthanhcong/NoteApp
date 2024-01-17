package com.example.noteapp.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.databinding.NoteItemBinding
import com.example.noteapp.models.Notes


class NoteAdapter(private val noteItemListener: NoteItemListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    var clickState: Int =
        0 // 0 là click update and open isert, 1 là long click chọn các item cần xóa
    private var listItemSelected: MutableList<Notes> = mutableListOf()
    private var countSelected: Int = 0  //Biến để đếm lần chọn thứ bao nhiêu

    private val differCallback = object : DiffUtil.ItemCallback<Notes>() {
        override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
            return oldItem.note == newItem.note
        }

        override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemBinding =
            NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = differ.currentList[position]
        holder.bind(note)
    }

    inner class NoteViewHolder(private val itemBinding: NoteItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(note: Notes) {

            itemBinding.tvTitle.text = note.title
            itemBinding.tvNote.text = note.note
            itemBinding.tvDate.text = note.date

            if (note.selectCheck) {
                //Nếu trạng thái chọn là true -> đổi background, thêm vào list chọn
                itemBinding.cardItem.setBackgroundResource(R.drawable.border_translate_background)
            } else {
                itemBinding.cardItem.setBackgroundResource(R.drawable.border_grey_background)
            }

            itemView.setOnClickListener {

                if (clickState == 1) {
                    //Nếu trạng thái click = 1
                    // -> được phép chọn nhiều item
                    // -> nhóm lại một list để thao tác
                    if (note.selectCheck) {
                        setupOnlyClickRemoveSelected(note, adapterPosition)
                    } else {
                        setupOnlyClickAddSelected(note, adapterPosition)
                    }
                } else {
                    //Open add note and update screen
                    noteItemListener.onClickOpenItemListener(note)
                }
            }
            itemView.setOnLongClickListener {
                clickState = 1 //Chuyển đổi trạng thái click sang chọn nhiều item
                if (note.selectCheck) {
                    setupLongClickRemoveSelectedItem(note, adapterPosition)
                } else {
                    setupLongClickAddSelected(note, adapterPosition)
                }
                true
            }
        }

        private fun randomColor(): Int {
            val rnd = java.util.Random()
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }

    }

    private fun removeItemAt(position: Int) {
        differ.currentList.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun setItemSelectChecked(position: Int, status: Boolean) {
        differ.currentList[position].selectCheck = status
        notifyItemChanged(position)
    }

    fun getSelectedItemList(): MutableList<Notes> {
        return listItemSelected
    }

    fun addSelectedItemList(note: Notes) {
        listItemSelected.add(note)
    }

    fun removeSelectedItemList(note: Notes) {
        listItemSelected.remove(note)
    }

    fun clearSelectedItemList() {
        listItemSelected.clear()
    }

    fun countNumberIncrease() {
        this.countSelected += 1
    }

    fun countNumberDecrease() {
        this.countSelected -= 1
    }

    fun resetWhenDeleteSuccess() {
        countSelected = 0
        clickState = 0
    }

    fun resetAllWhenAddNewNote() {
        countSelected = 0
        clickState = 0
        clearSelectedItemList()

        for (i in differ.currentList.indices) {
            if (differ.currentList[i].selectCheck) {
                differ.currentList[i].selectCheck = false
                notifyItemChanged(i)
            }
        }

    }

    private fun setupLongClickRemoveSelectedItem(note: Notes, position: Int) {
        countNumberDecrease()//Cứ một lần chọn thì giảm đi 1
        //Nếu item đang được chọn thì bỏ chọn -> false
        setItemSelectChecked(position, false)
        //Xóa item khỏi danh sách chọn
        removeSelectedItemList(note)

        if (countSelected == 0) {
            //Nếu không còn item nào được chọn thì
            // Ẩn top bar, chuyển trạng thái open
            clickState = 0
            Log.d("CHECK_LOG_DISPLAY_", " Hide Top Bar ")
            noteItemListener.onHideTopActionBar()
        }
    }

    private fun setupLongClickAddSelected(note: Notes, position: Int) {
        countNumberIncrease()//Cứ một lần chọn thì tăng lên 1
        //Nếu item không được chọn thì chuyển sang chọn -> true
        setItemSelectChecked(position, true)
        //Thêm item vào danh sách chọn
        addSelectedItemList(note)

        if (countSelected == 1) {
            //Show Top Bar (chỉ show trong lần đầu chọn)
            Log.d("CHECK_LOG_DISPLAY_", " Show Top Bar ")
            noteItemListener.onShowTopActionBar()
        }
    }

    private fun setupOnlyClickRemoveSelected(note: Notes, position: Int) {
        //Nếu đã chọn -> bỏ chọn, xóa khỏi danh sách chọn
        countNumberDecrease()
        setItemSelectChecked(position, false)
        removeSelectedItemList(note)
        if (countSelected == 0) {
            //Nếu ko còn item được chọn -> clickState = 0
            // -> chuyển về chế độ open note
            // -> Ẩn Top bar
            clickState = 0
            Log.d("CHECK_LOG_DISPLAY_", " Hide Top Bar ")
            noteItemListener.onHideTopActionBar()
        }
    }

    private fun setupOnlyClickAddSelected(note: Notes, position: Int) {
        countNumberIncrease()
        //Nếu chưa chọn -> chọn item, thêm vào danh sách chọn
        setItemSelectChecked(position, true)
        addSelectedItemList(note)
    }

    interface NoteItemListener {
        fun onClickOpenItemListener(note: Notes)
        fun onHideTopActionBar()
        fun onShowTopActionBar()

    }
}