# -*- coding: utf-8 -*-

import requests
import pandas as pd
import numpy as np
import json

class StockModel():
    def __init__(self, date, price):
        self.date = date
        self.price = price

class StockData:
    
    def __init__(self, trade_code):
        self.code = trade_code
    
    def fetch(self, months_count, date_index = False):
        url = 'https://www.dsebd.org/php_graph/monthly_graph.php?inst=' + self.code + '&duration=' + str(months_count) + '&type=price'
        r = requests.get(url)
        r.text
        data = r.text
        #companyCode = data.split("Closing Price Graph of ",1)[1].split('\'')[0]
        d = data.split('\\n')
        del d[0]
        del d[-1]
        for i in range(0, len(d)):
            if(i == 0):
                d[i] = d[i].split('" +\r\n    "')[1]
            else:
                d[i] = d[i].split('"+"')[1]
    
        stock2dList = []
        for i in range(len(d)):
            arr = d[i].split(",")
            arr[1] = float(arr[1])
            stock2dList.append(arr)
    
        df = pd.DataFrame(stock2dList)
        df.columns =['Date','Price']
    
        print("---------------------------------")
        print("Trading Code: " + self.code)
        print("---------------------------------")
        print("Days: " + str(df.shape[0]))
        print("---------------------------------")
        if date_index:
            df = df.set_index(pd.to_datetime(df['Date']))
            del df['Date']
        return df
    
    def format_df_json(self, df, format_json = True): 
        arr = np.array(df)
        stock_list = []
        for i in range(len(arr)):
            sm = StockModel(arr[i][0], arr[i][1])
            stock_list.append(sm.__dict__)
        if format_json:
            return json.dumps(stock_list)
        else:
            return stock_list