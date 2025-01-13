package com.example.gomind.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomind.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    private List<String> answers; // Список ответов (A, B, C, D)
    public interface  AnswerClickListener {
        void onClickListener(int answerId);
    }

    private AnswerClickListener clickListener;

    private int selectedItemIndex = -1;
    // В конструктор передаем список ответов и обработчик нажатия
    public AnswersAdapter(List<String> answers, AnswerClickListener clickListener) {
        this.answers = answers;
        this.clickListener = clickListener;

    }

    @Override
    public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Раздуваем layout для элемента списка (кнопки)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnswerViewHolder holder, int position) {

        // Устанавливаем текст на кнопке
        holder.answerButton.setText(answers.get(position));
//        holder.answerButton.setTag(position); // Устанавливаем индекс ответа как тег
        if(position == selectedItemIndex){
            holder.answerButton.setBackgroundResource(R.drawable.auth_button);
        }
        else {
            holder.answerButton.setBackgroundResource(R.drawable.border_inside);
        }

        holder.answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition(); // актуальная позиция чтобы ресайкл не выделывался на инт позишн в конструкторе
            if (currentPosition == RecyclerView.NO_POSITION){
                return; // просто выходим
            }
            if (currentPosition == selectedItemIndex) {
                // Если элемент уже выбран, сбрасываем выбор
                selectedItemIndex = -1;
            } else {
                // Устанавливаем новый выбранный элемент
                selectedItemIndex = currentPosition;
            }
//            notifyDataSetChanged(); // Обновляем адаптер, чтобы отобразить и
                selectedItemIndex = holder.getAdapterPosition();
                clickListener.onClickListener(position+1);
                notifyDataSetChanged();
            }
        });


//        holder.answerButton.setOnClickListener(v->{
//            int currentPosition = holder.getAdapterPosition(); // актуальная позиция чтобы ресайкл не выделывался на инт позишн в конструкторе
//            if (currentPosition == RecyclerView.NO_POSITION){
//                return; // просто выходим
//            }
//            if (currentPosition == selectedItemIndex) {
//                // Если элемент уже выбран, сбрасываем выбор
//                selectedItemIndex = -1;
//            } else {
//                // Устанавливаем новый выбранный элемент
//                selectedItemIndex = currentPosition;
//            }
//            notifyDataSetChanged(); // Обновляем адаптер, чтобы отобразить изменения
//        });
       }





    @Override
    public int getItemCount() {
        return answers.size(); // Количество элементов в списке (количество ответов)
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        MaterialButton answerButton;

        public AnswerViewHolder(View itemView) {
            super(itemView);
            answerButton = itemView.findViewById(R.id.btn_long_answer);
        }
    }
}

