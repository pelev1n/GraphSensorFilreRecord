package com.example.user.grafacc;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {
    private List<Sensor> sensors;
    private LayoutInflater mIflater;
    private Context mContext;

    public SensorAdapter(Context context, List<Sensor> sensorList) {
    this.mContext=context;
    this.sensors=sensorList;
    this.mIflater=LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      View view=mIflater.inflate(R.layout.adapter_item,viewGroup,false);

        view.setOnClickListener((View.OnClickListener) mContext);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    final Sensor sensor=sensors.get(i);
    String[] finalSensorName = sensors.get(i).getName().toString().split(" ");
    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!" + sensor);
    System.out.println(Integer.toString(sensor.getType()));
    System.out.println(sensor.getVendor());
    System.out.println(sensor.getVersion());
    System.out.println((int) sensor.getMaximumRange());

    viewHolder.name.setText(finalSensorName[2]);
        viewHolder.type.setText("Type: " + Integer.toString(sensor.getType()));
        viewHolder.vendor.setText(sensor.getVendor());
        viewHolder.version.setText("Version: " + Integer.toString(sensor.getVersion()));
        viewHolder.max.setText("Max Range: " + Float.toString( sensor.getMaximumRange()));
        viewHolder.resolution.setText("Resolution: " + Float.toString((sensor.getResolution())));
        /*viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, GrafActivity.class);
              Integer i=sensor.getType();
//              String s=sensor.getStringType();
                    intent.putExtra("sensortype",sensor.getType());

                mContext.startActivity(intent);
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView type;
        TextView vendor;
        TextView version;
        TextView max;
        TextView resolution;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.name);
            type=(TextView) itemView.findViewById(R.id.type);
            vendor=(TextView)itemView.findViewById(R.id.vendor);
            version=(TextView) itemView.findViewById(R.id.version);
            max=(TextView) itemView.findViewById(R.id.max);
            resolution=(TextView) itemView.findViewById(R.id.resolution);

        }
    }
}
