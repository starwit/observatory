from datetime import datetime
from pathlib import Path
from typing import List

import pandas as pd

def read_area_csv(file: Path, area_filter: List[str]) -> pd.DataFrame:
    area_df = pd.read_csv(file)
    # Drop all uninteresting columns
    area_df = area_df[['occupancy_time', 'name', 'count']]
    area_df['occupancy_time'] = pd.to_datetime(area_df['occupancy_time'], format='ISO8601')
    area_df = area_df.loc[area_df['name'].isin(area_filter)]

    # Create time buckets by flooring the time value
    area_df['occupancy_time_buckets'] = area_df['occupancy_time'].dt.floor('10s')

    # Pivot area names into columns and sum
    pivoted_area_df = area_df.pivot_table(values='count', index='occupancy_time_buckets', columns='name', aggfunc='mean')
    pivoted_area_df['total_count'] = pivoted_area_df.sum(axis=1)

    return pivoted_area_df

def read_line_csv(file: Path, line_filter: str, start_time: datetime = None) -> pd.DataFrame:
    line_df = pd.read_csv(file)
    line_df = line_df[['crossing_time', 'name', 'direction']]
    line_df['crossing_time'] = pd.to_datetime(line_df['crossing_time'], format='ISO8601')
    line_df = line_df.loc[line_df['name'] == line_filter]
    if start_time is not None:
        line_df = line_df[line_df['crossing_time'] > start_time]
    return line_df