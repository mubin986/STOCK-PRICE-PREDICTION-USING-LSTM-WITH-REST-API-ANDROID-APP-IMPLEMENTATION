#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Jan 24 11:14:12 2020

@author: Mubin
"""
from flask import Flask, request, jsonify
import numpy as np
import pickle
from stock_data import StockData
import json
import os

app = Flask(__name__)

files = os.listdir('./json')

stocks = []
for i in range(len(files)):
    data_type = os.path.splitext(files[i])[1]
    if data_type == '.json':
        file_name = os.path.splitext(files[i])[0]
        stocks.append(file_name)

stocks_json = {}
for i in range(len(stocks)):
    with open('./json/' + stocks[i] + '.json') as json_file:
        data = json.load(json_file)
        stocks_json[stocks[i]] = data

scalers = {}
models = {}
for i in range(len(stocks)):
    scaler = pickle.load(open('./pickles/' + stocks[i] + '_scaler.pkl', 'rb'))
    model = pickle.load(open('./pickles/' + stocks[i] + '.pkl', 'rb'))
    scalers[stocks[i]] = scaler
    models[stocks[i]] = model

@app.route('/')
def home():
    return "DSE Stock Prediction API"

@app.route('/stocks')
def s():
    message = {
        'total' : len(stocks),
        'stocks' : stocks
    }
    resp = jsonify(message)
    resp.status_code = 200
    print(resp)
    return resp

@app.route('/data/<path:pars>')
def stock_data(pars):
    print(pars)
    trade_code = '/'.join(pars.split("/",2)[:1])
    month_count = pars.split("/",2)[1:][0]
    #return trade_code + '--' + month_count
    sd = StockData(trade_code)
    data = sd.fetch(month_count, date_index=False)
    return jsonify(sd.format_df_json(data, False))

@app.route('/predict/<path:pars>')
def predict(pars):
    trade_code = '/'.join(pars.split("/",2)[:1])
    
    sd = StockData(trade_code)
    df = sd.fetch(4, date_index=True)
    df = df.tail(stocks_json[trade_code]['size'])
    #return jsonify(sd.format_df_json(last_n_days_df, False))
    #return str(df.shape + ' scaled')
    df_scaled = scalers[trade_code].transform(df)
    pred_test = []
    pred_test.append(df_scaled)
    pred_test = np.array(pred_test)
    pred_test = np.reshape(pred_test, (pred_test.shape[0], pred_test.shape[1], 1))
    pred_price = models[trade_code].predict(pred_test)
    pred_price = scalers[trade_code].inverse_transform(pred_price)
    
    message = {
        'prediction' : str(pred_price[0][0]),
        'meta' : stocks_json[trade_code]
    }
    
    resp = jsonify(message)
    resp.status_code = 200
    print(resp)
    return resp


if __name__ == "__main__":
    #app.run(debug=True, host='10.24.135.89')
    app.run(host='192.168.201.4')
    #app.run()