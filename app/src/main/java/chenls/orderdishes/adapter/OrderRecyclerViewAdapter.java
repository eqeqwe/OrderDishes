package chenls.orderdishes.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import chenls.orderdishes.R;
import chenls.orderdishes.bean.Dish;
import chenls.orderdishes.bean.Order;
import chenls.orderdishes.fragment.OrderFragment;


public class OrderRecyclerViewAdapter extends
        RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private final List<Order> orderList;
    private final OrderFragment.OnListFragmentInteractionListener mListener;

    public OrderRecyclerViewAdapter(Context context, List<Order> orderList
            , OrderFragment.OnListFragmentInteractionListener listener) {
        this.context = context;
        this.orderList = orderList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Order order = orderList.get(position);
        Map<Integer, Dish> dishMap = order.getDishMap();
        Dish dish = null;
        for (Dish d : dishMap.values()) {
            dish = d;
            break;
        }
        Glide.with(context)
                .load(dish.getPic().getFileUrl(context))
                .crossFade()
                .placeholder(R.mipmap.loading)
                .into(holder.iv_dish);
        holder.tv_dish_name.setText(dish.getName() + "等");
        holder.tv_time.setText(order.getCreatedAt());
        String[] state = context.getResources().getStringArray(R.array.state);
        holder.tv_state.setText(state[order.getState()]);
        holder.tv_price.setText(context.getString(R.string.total_price, order.getPrice() + ""));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOrderListButtonClick(order);
            }
        });
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除订单
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.delete_order));
                builder.setMessage(context.getString(R.string.sure_delete));
                builder.setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDeleteButtonClick(order.getObjectId());
                    }
                });
                builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView iv_dish, iv_delete;
        public final TextView tv_dish_name, tv_time, tv_state, tv_price;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            iv_dish = (ImageView) view.findViewById(R.id.iv_dish);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            tv_dish_name = (TextView) view.findViewById(R.id.tv_dish_name);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_state = (TextView) view.findViewById(R.id.tv_state);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
        }
    }
}
